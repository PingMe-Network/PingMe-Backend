package me.huynhducphu.PingMe_Backend.service.chat.util;

import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomParticipantResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.model.chat.Message;
import me.huynhducphu.PingMe_Backend.model.chat.Room;
import me.huynhducphu.PingMe_Backend.model.chat.RoomParticipant;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Admin 8/30/2025
 *
 **/
@Component
public class ChatDtoUtils {

    public static MessageResponse toMessageResponseDto(Message message) {

        String clientMsgId = message.getClientMsgId() == null ? null : message.getClientMsgId().toString();
        String content = message.isActive() ? message.getContent() : null;

        return new MessageResponse(
                message.getId(),
                message.getRoom().getId(),
                clientMsgId,
                message.getSender().getId(),
                content,
                message.getType(),
                message.getCreatedAt(),
                message.isActive()
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
        return res;
    }

}
