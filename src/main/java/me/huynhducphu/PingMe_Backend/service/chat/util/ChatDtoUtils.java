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

    public MessageResponse toMessageResponseDto(Message message) {
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

    public RoomResponse toRoomResponseDto(
            Room room,
            List<RoomParticipant> roomParticipants,
            Long userId
    ) {
        List<RoomParticipantResponse> roomParticipantResponses = roomParticipants
                .stream()
                .map(rp -> new RoomParticipantResponse(
                        rp.getUser().getId(),
                        rp.getUser().getName(),
                        rp.getUser().getAvatarUrl(),
                        rp.getRole(),
                        rp.getLastReadMessageId(),
                        rp.getLastReadAt()
                ))
                .toList();

        Long currentUserLastReadIdMessage = roomParticipants.stream()
                .filter(rp -> rp.getUser().getId().equals(userId))
                .findFirst()
                .map(RoomParticipant::getLastReadMessageId)
                .orElse(null);

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
