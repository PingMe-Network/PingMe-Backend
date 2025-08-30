package me.huynhducphu.PingMe_Backend.service.chat.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.MarkReadRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.ReadStateResponse;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.MessageCreatedEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.RoomUpdatedEvent;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;
import me.huynhducphu.PingMe_Backend.model.common.RoomMemberId;
import me.huynhducphu.PingMe_Backend.model.constant.MessageType;
import me.huynhducphu.PingMe_Backend.repository.MessageRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomParticipantRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomRepository;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.chat.util.ChatDtoUtils;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher eventPublisher;

    private final ChatDtoUtils chatDtoUtils;

    @Override
    public MessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
        // Lấy người dùng gửi tin nhắn
        var currentUser = currentUserProvider.get();

        // Trích xuất ra thông tin người gửi
        // + Mã người dùng
        // + Mã phòng chat người đã gửi
        var senderId = currentUser.getId();
        var roomId = sendMessageRequest.getRoomId();

        // Kiểm tra loại tin nhắn người đó gửi (TEXT, FILE, IMG, ...)
        if (sendMessageRequest.getType() != MessageType.TEXT)
            validateUrl(sendMessageRequest.getContent());

        // Kiểm tra clientMsgId có hợp lệ không
        // clientMsgId tránh người dùng spam khi
        // đường truyền không ổn định
        UUID clientMsgId;
        try {
            clientMsgId = UUID.fromString(sendMessageRequest.getClientMsgId());
        } catch (Exception exception) {
            throw new IllegalArgumentException("clientMsg không hợp lệ");
        }

        // Tìm phòng chat mà người dùng đã gửi tin nhắn
        // Nếu tìm không được trả về lỗi
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, senderId);
        if (!roomParticipantRepository.existsById(roomMemberId))
            throw new AccessDeniedException("Bạn không phải thành viên của phòng chat này");

        // Kiểm tra tin nhắn người dùng gửi đã tồn tại chưa, Kiểm tra bằng mã clientMsgId
        // Nếu tìm thấy thì trả về tin nhắn đã tồn tại trong cơ sở dữ liệu
        var existed = messageRepository
                .findByRoom_IdAndSender_IdAndClientMsgId(roomId, senderId, clientMsgId)
                .orElse(null);
        if (existed != null) return chatDtoUtils.toMessageResponseDto(existed);

        // Nếu chưa tồn tại, tạo tin nhắn mới
        Message message = new Message();
        message.setRoom(room);
        message.setSender(userRepository.getReferenceById(senderId));
        message.setContent(sendMessageRequest.getContent());
        message.setType(sendMessageRequest.getType());
        message.setClientMsgId(clientMsgId);
        message.setCreatedAt(LocalDateTime.now());

        // Lưu tin nhắn vào cơ sở dữ liệu
        // Thực hiện Try Catch để tránh Race Condition
        // cho trường hợp User dùng 2 máy gửi tin nhắn cùng lúc
        try {
            message = messageRepository.save(message);
        } catch (DataIntegrityViolationException ex) {
            message = messageRepository
                    .findByRoom_IdAndSender_IdAndClientMsgId(roomId, senderId, clientMsgId)
                    .orElseThrow(() -> ex);
        }

        // Cập nhật trạng thái phòng khi người dùng nhắn tin
        // Thông tin cập nhật bao gồm:
        // + Tin nhắn cuối cùng
        // + Thời gian nhắn tin nhắn cuối cùng
        room.setLastMessage(message);
        room.setLastMessageAt(message.getCreatedAt());
        var savedRoom = roomRepository.save(room);

        // Cập nhật trạng thái của người dùng tham gia phòng (người dùng hiện tại)
        // Thông tin cập nhật bao gồm:
        // + Tin nhắn lần cuối đọc (seen)
        // + Thời gian đọc tin nhắn cuối cùng
        //
        // Dễ hiểu: người dùng A chat tin nhắn đó, thì mặc
        // định người dùng A đọc tất cả tin nhắn từ đầu đến tin nhắn
        // hiện tại của người dùng A
        Message finalMsg = message;
        roomParticipantRepository
                .findById(roomMemberId)
                .ifPresent(rp -> {
                    rp.setLastReadMessageId(finalMsg.getId());
                    rp.setLastReadAt(finalMsg.getCreatedAt());
                });

        // ===================================================================================================
        // WEBSOCKET

        // Sự kiện MESSAGE_CREATED (tạo tin nhắn mới)
        var messageCreatedEvent = new MessageCreatedEvent(message);

        // Sử kiện ROOM_UPDATED (thông báo phòng có tin nhắn mới)
        var roomUpdatedEvent = new RoomUpdatedEvent(
                savedRoom, // Dữ liệu mới nhất của phòng
                roomParticipantRepository.findByRoom_Id(savedRoom.getId()) // Người trong phòng chat
        );

        // Bắn sự kiện Websocket
        eventPublisher.publishEvent(messageCreatedEvent);
        eventPublisher.publishEvent(roomUpdatedEvent);
        // ===================================================================================================

        // Trả dữ liệu về cho người gửi tin nhắn
        return chatDtoUtils.toMessageResponseDto(message);
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
                .map(chatDtoUtils::toMessageResponseDto)
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

}
