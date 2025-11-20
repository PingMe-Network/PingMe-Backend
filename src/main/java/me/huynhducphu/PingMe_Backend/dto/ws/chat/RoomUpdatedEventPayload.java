package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.BaseChatEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.ChatEventType;

/**
 * Admin 8/30/2025
 *
 **/
@Getter
public class RoomUpdatedEventPayload extends BaseChatEventPayload {

    private final RoomResponse roomResponse;

    public RoomUpdatedEventPayload(RoomResponse roomResponse) {
        super(ChatEventType.ROOM_UPDATED);
        this.roomResponse = roomResponse;
    }
}
