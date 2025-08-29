package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;

/**
 * Admin 8/29/2025
 *
 **/
@Data
@NoArgsConstructor
public class RoomUpdatedEvent {

    private ChatEventType chatEventType = ChatEventType.ROOM_UPDATED;
    private Long userId;
    private Long roomId;
    private RoomResponse roomResponse;

    public RoomUpdatedEvent(Long userId, RoomResponse roomResponse, Long roomId) {
        this.userId = userId;
        this.roomResponse = roomResponse;
        this.roomId = roomId;
    }
}
