package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Getter;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
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
    private final MessageResponse systemMessage;

    public RoomUpdatedEventPayload(RoomResponse roomResponse, MessageResponse systemMessage) {
        super(ChatEventType.ROOM_UPDATED);
        this.roomResponse = roomResponse;
        this.systemMessage = systemMessage;
    }
}
