package me.huynhducphu.PingMe_Backend.service.chat.util;

import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomParticipantResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Admin 8/30/2025
 *
 **/
@Component
public class ChatDtoUtils {

    public static MessageResponse toMessageResponseDto(Message message) {
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

    public static RoomResponse toRoomResponseDto(
            Room room,
            List<RoomParticipant> roomParticipants,
            Long userId
    ) {
        // Lấy danh sách thành viên trong phòng chat
        List<RoomParticipantResponse> roomParticipantResponses = roomParticipants
                .stream()
                .map(rp -> new RoomParticipantResponse(
                        rp.getUser().getId(),
                        rp.getUser().getName(),
                        rp.getUser().getAvatarUrl(),
                        rp.getUser().getStatus(),
                        rp.getRole(),
                        rp.getLastReadMessageId(),
                        rp.getLastReadAt()
                ))
                .toList();

        // Lấy tin nhắn id mà người dùng hiện tại đọc tới
        Long currentUserLastReadIdMessage = roomParticipants.stream()
                .filter(rp -> rp.getUser().getId().equals(userId))
                .findFirst()
                .map(RoomParticipant::getLastReadMessageId)
                .orElse(null);

        // Tính số tin nhắn "chưa đọc" bằng chênh lệch ID.
        // - Nếu phòng chưa có tin nhắn -> 0
        // - Nếu có tin nhắn và user chưa từng đọc -> dùng ID tin nhắn mới nhất làm số lượng
        // - Ngược lại -> unread = max(0, lastMsgId - lastReadId)
        long unread = 0;
        if (room.getLastMessage() != null) {
            long lastMsgId = room.getLastMessage().getId();
            unread = (currentUserLastReadIdMessage == null)
                    ? lastMsgId
                    : Math.max(0, lastMsgId - currentUserLastReadIdMessage);
        }

        RoomResponse.LastMessage lastMessage = null;
        if (room.getLastMessage() != null) {
            Message message = room.getLastMessage();
            lastMessage = new RoomResponse.LastMessage(
                    message.getId(),
                    message.getSender().getId(),
                    message.getContent(),
                    message.getType(),
                    message.getCreatedAt()
            );
        }

        RoomResponse res = new RoomResponse();
        res.setRoomId(room.getId());
        res.setRoomType(room.getRoomType());
        res.setDirectKey(room.getDirectKey());
        res.setName(room.getName());
        res.setLastMessage(lastMessage);
        res.setParticipants(roomParticipantResponses);
        res.setUnreadCount(unread);
        return res;
    }

}
