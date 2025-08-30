package me.huynhducphu.PingMe_Backend.dto.ws.chat.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.ChatEventType;

/**
 * Admin 8/30/2025
 *
 **/
@Data
@NoArgsConstructor
public class RoomUpdatedEventPayload {

    private ChatEventType chatEventType = ChatEventType.ROOM_UPDATED;
    private RoomResponse roomResponse;

    public RoomUpdatedEventPayload(RoomResponse roomResponse) {
        this.roomResponse = roomResponse;
    }
}
