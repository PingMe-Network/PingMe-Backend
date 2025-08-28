package me.huynhducphu.PingMe_Backend.service.chat.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.MarkReadRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.ReadStateResponse;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;
import me.huynhducphu.PingMe_Backend.model.common.RoomMemberId;
import me.huynhducphu.PingMe_Backend.model.constant.MessageType;
import me.huynhducphu.PingMe_Backend.repository.MessageRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomParticipantRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomRepository;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Admin 8/26/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements me.huynhducphu.PingMe_Backend.service.chat.MessageService {

    private final CurrentUserProvider currentUserProvider;

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
        var currentUser = currentUserProvider.get();

        var senderId = currentUser.getId();
        var roomId = sendMessageRequest.getRoomId();

        if (sendMessageRequest.getType() != MessageType.TEXT)
            validateUrl(sendMessageRequest.getContent());

        UUID clientMsgId;
        try {
            clientMsgId = UUID.fromString(sendMessageRequest.getClientMsgId());
        } catch (Exception exception) {
            throw new IllegalArgumentException("clientMsg không hợp lệ");
        }

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, senderId);
        if (!roomParticipantRepository.existsById(roomMemberId))
            throw new AccessDeniedException("Bạn không phải thành viên của phòng chat này");

        var existed = messageRepository
                .findByRoom_IdAndSender_IdAndClientMsgId(roomId, senderId, clientMsgId)
                .orElse(null);
        if (existed != null) return toDto(existed);

        Message msg = new Message();
        msg.setRoom(room);
        msg.setSender(userRepository.getReferenceById(senderId));
        msg.setContent(sendMessageRequest.getContent());
        msg.setType(sendMessageRequest.getType());
        msg.setClientMsgId(clientMsgId);
        msg.setCreatedAt(LocalDateTime.now());

        try {
            msg = messageRepository.save(msg);
        } catch (DataIntegrityViolationException ex) {
            msg = messageRepository
                    .findByRoom_IdAndSender_IdAndClientMsgId(roomId, senderId, clientMsgId)
                    .orElseThrow(() -> ex);
        }

        room.setLastMessage(msg);
        room.setLastMessageAt(msg.getCreatedAt());
        roomRepository.save(room);

        Message finalMsg = msg;
        roomParticipantRepository.findById(roomMemberId).ifPresent(rp -> {
            rp.setLastReadMessageId(finalMsg.getId());
            rp.setLastReadAt(finalMsg.getCreatedAt());
        });

        return toDto(msg);
    }

    @Override
    public ReadStateResponse markAsRead(MarkReadRequest markReadRequest) {
        var currentUser = currentUserProvider.get();

        Long userId = currentUser.getId();
        Long roomId = markReadRequest.getRoomId();
        Long lastReadMessageId = markReadRequest.getLastReadMessageId();

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, userId);
        RoomParticipant roomParticipant = roomParticipantRepository
                .findById(roomMemberId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên của phòng chat này"));

        var msgOpt = messageRepository.findById(lastReadMessageId);
        if (msgOpt.isEmpty() || !msgOpt.get().getRoom().getId().equals(roomId))
            throw new IllegalArgumentException("tin nhắn không thuộc phòng này");

        Long newPointer = (roomParticipant.getLastReadMessageId() == null)
                ? lastReadMessageId
                : Math.max(roomParticipant.getLastReadMessageId(), lastReadMessageId);

        roomParticipant.setLastReadMessageId(newPointer);
        roomParticipant.setLastReadAt(LocalDateTime.now());

        long unread = 0L;
        if (room.getLastMessage() != null) {
            long lastMsgId = room.getLastMessage().getId();
            unread = Math.max(0, lastMsgId - newPointer);
        }

        return new ReadStateResponse(roomId, userId, newPointer, roomParticipant.getLastReadAt(), unread);
    }

    @Override
    public List<MessageResponse> getHistoryMessages(
            Long roomId,
            Long beforeId,
            Integer size
    ) {
        if (roomId == null || size == null)
            throw new IllegalArgumentException("Mã phòng hoặc số lượng tin nhắn yêu cầu không hợp lệ");

        var currentUser = currentUserProvider.get();
        var userId = currentUser.getId();
        
        roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, userId);
        if (!roomParticipantRepository.existsById(roomMemberId))
            throw new AccessDeniedException("Bạn không phải thành viên của phòng chat này");

        int fixedSize = Math.max(1, Math.min(size, 20));
        Pageable limit = PageRequest.of(0, fixedSize);

        return messageRepository
                .findHistoryMessagesByKeySet(roomId, beforeId, limit)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =====================================
    // Utilities methods
    // =====================================
    private static void validateUrl(String url) {
        try {
            URI u = URI.create(url);
            if (u.getScheme() == null || u.getHost() == null)
                throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
        }
    }

    private MessageResponse toDto(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRoom().getId(),
                (message.getClientMsgId() == null ? null : message.getClientMsgId().toString()),
                message.getSender().getId(),
                message.getContent(),
                message.getType(),
                message.getCreatedAt()
        );
    }

}
