package org.ping_me.service.call.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ping_me.dto.request.call.SignalingPayload;
import org.ping_me.dto.request.call.SignalingRequest;
import org.ping_me.dto.response.call.SignalingResponse;
import org.ping_me.model.common.RoomMemberId;
import org.ping_me.repository.jpa.chat.RoomParticipantRepository;
import org.ping_me.service.call.SignalingService;
import org.ping_me.service.user.CurrentUserProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignalingServiceImpl implements SignalingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomParticipantRepository roomParticipantRepository;
    private final CurrentUserProvider currentUserProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration SESSION_TTL = Duration.ofHours(1);
    private static final String PARTICIPANTS_KEY = "callSession:%s:participants";
    private static final String META_KEY        = "callSession:%s:meta";
    private static final String SIGNALING_QUEUE = "/queue/signaling";

    @Override
    public void processSignaling(SignalingRequest request) {
        var currentUser = currentUserProvider.get();
        Long   senderId   = currentUser.getId();
        String senderName = currentUser.getName();
        Long   roomId     = request.getRoomId();
        String type       = request.getType();
        String sessionId  = request.getCallSessionId();

        log.info("SIGNALING: {} | From: {} ({}) | Room: {} | Session: {}",
                type, senderId, senderName, roomId, sessionId);

        // ── 1. Guard: callSessionId bắt buộc với các type cần state ──────────
        boolean requiresSession = switch (type) {
            case "INVITE", "ACCEPT", "LEAVE", "HANGUP" -> true;
            default -> false;
        };

        if (requiresSession && (sessionId == null || sessionId.isBlank())) {
            log.warn("Signal {} từ user {} thiếu callSessionId — bỏ qua", type, senderId);
            return;
        }

        // ── 2. Kiểm tra thành viên phòng ─────────────────────────────────────
        boolean isMember = roomParticipantRepository
                .existsById(new RoomMemberId(roomId, senderId));

        if (!isMember) {
            boolean isCleanupSignal = switch (type) {
                case "REJECT", "HANGUP", "LEAVE" -> true;
                default -> false;
            };
            if (!isCleanupSignal) {
                log.warn("User {} bị chặn khi gửi {} vào room {}", senderId, type, roomId);
                throw new AccessDeniedException("Bạn không thuộc phòng chat này");
            }
            log.warn("User {} không trong room {} nhưng cho phép {} để cleanup",
                    senderId, roomId, type);
        }

        // ── 3. Xử lý Redis theo từng loại signal ─────────────────────────────
        int activeParticipantCount = 0;

        try {
            activeParticipantCount = switch (type) {
                case "INVITE"  -> handleInvite(sessionId, senderId, roomId, request.getPayload());
                case "ACCEPT"  -> handleAccept(sessionId, senderId, senderName, roomId);
                case "LEAVE"   -> handleLeave(sessionId, senderId);
                case "HANGUP"  -> handleHangup(sessionId);
                default        -> 0; // REJECT — không cần Redis
            };
        } catch (SessionEndedException e) {
            // Session đã hết hạn khi user cố ACCEPT → thông báo ngược lại cho người gửi
            log.warn("Session {} đã kết thúc, user {} không thể join", sessionId, senderId);
            sendToUser(senderId, new SignalingResponse(
                    "SESSION_ENDED", senderId, senderName, roomId, sessionId, 0, null));
            return;
        } catch (Exception e) {
            log.error("Lỗi xử lý Redis cho signal {} session {}: {}", type, sessionId, e.getMessage());
            // Không chặn signal, vẫn forward để FE không bị treo
        }

        // ── 4. Tìm người nhận ────────────────────────────────────────────────
        List<Long> otherParticipantIds =
                roomParticipantRepository.findAllUserIdsInRoomExcept(roomId, senderId);

        if (otherParticipantIds.isEmpty()) {
            log.warn("Không tìm thấy người nhận trong room {}", roomId);
            return;
        }
        if ("INVITE".equals(type)) {
            activeParticipantCount = otherParticipantIds.size() + 1;
        }

        // ── 5. Build response và broadcast ───────────────────────────────────
        SignalingResponse response = new SignalingResponse(
                type,
                senderId,
                senderName,
                roomId,
                sessionId,
                activeParticipantCount,
                request.getPayload()
        );

        for (Long targetId : otherParticipantIds) {
            log.info("-> Forwarding {} | Session {} -> User {}", type, sessionId, targetId);
            sendToUser(targetId, response);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private Handlers
    // ─────────────────────────────────────────────────────────────────────────

    private int handleInvite(String sessionId, Long senderId, Long roomId,
                             SignalingPayload payload) {
        String participantsKey = PARTICIPANTS_KEY.formatted(sessionId);
        String metaKey         = META_KEY.formatted(sessionId);

        // Tạo Set participants, thêm người khởi tạo
        redisTemplate.opsForSet().add(participantsKey, String.valueOf(senderId));
        redisTemplate.expire(participantsKey, SESSION_TTL);

        // Lưu meta
        try {
            String callType = payload != null ? payload.getCallType() : "VIDEO";
            String meta = objectMapper.writeValueAsString(Map.of(
                    "roomId",   roomId,
                    "status",   "ONGOING",
                    "callType", callType != null ? callType : "VIDEO"
            ));
            redisTemplate.opsForValue().set(metaKey, meta, SESSION_TTL);
        } catch (JsonProcessingException e) {
            log.warn("Không thể serialize meta cho session {}", sessionId);
        }

        log.info("Session {} CREATED | Room: {} | Initiator: {}", sessionId, roomId, senderId);
        return 1; // Chỉ có người gọi, chưa ai accept
    }

    private int handleAccept(String sessionId, Long senderId,
                             String senderName, Long roomId) {
        String participantsKey = PARTICIPANTS_KEY.formatted(sessionId);

        // Validate session còn sống không
        Boolean sessionExists = redisTemplate.hasKey(participantsKey);
        if (Boolean.FALSE.equals(sessionExists)) {
            throw new SessionEndedException(sessionId);
        }

        // Thêm vào Set (SADD tự bỏ qua nếu đã có → an toàn khi reconnect)
        redisTemplate.opsForSet().add(participantsKey, String.valueOf(senderId));

        // Reset TTL mỗi khi có người mới join
        redisTemplate.expire(participantsKey, SESSION_TTL);
        redisTemplate.expire(META_KEY.formatted(sessionId), SESSION_TTL);

        Long count = redisTemplate.opsForSet().size(participantsKey);
        int activeCount = count != null ? count.intValue() : 1;

        log.info("Session {} | User {} JOINED | Active: {}", sessionId, senderId, activeCount);
        return activeCount;
    }

    private int handleLeave(String sessionId, Long senderId) {
        String participantsKey = PARTICIPANTS_KEY.formatted(sessionId);

        redisTemplate.opsForSet().remove(participantsKey, String.valueOf(senderId));
        Long remaining = redisTemplate.opsForSet().size(participantsKey);
        int remainingCount = remaining != null ? remaining.intValue() : 0;

        if (remainingCount == 0) {
            redisTemplate.delete(participantsKey);
            redisTemplate.delete(META_KEY.formatted(sessionId));
            log.info("Session {} ENDED — người cuối cùng ({}) đã rời", sessionId, senderId);
        } else {
            log.info("Session {} | User {} LEFT | Còn lại: {}", sessionId, senderId, remainingCount);
        }

        return remainingCount;
    }

    private int handleHangup(String sessionId) {
        redisTemplate.delete(PARTICIPANTS_KEY.formatted(sessionId));
        redisTemplate.delete(META_KEY.formatted(sessionId));
        log.info("Session {} FORCE ENDED (HANGUP)", sessionId);
        return 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void sendToUser(Long userId, SignalingResponse response) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                SIGNALING_QUEUE,
                response
        );
    }

    // Exception nội bộ — chỉ dùng trong service này
    private static class SessionEndedException extends RuntimeException {
        SessionEndedException(String sessionId) {
            super("Session ended: " + sessionId);
        }
    }
}