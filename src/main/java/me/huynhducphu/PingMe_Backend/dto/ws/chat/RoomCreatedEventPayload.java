package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Getter;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.BaseChatEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.ChatEventType;

/**
 * Admin 11/20/2025
 *
 **/
@Getter
public class RoomCreatedEventPayload extends BaseChatEventPayload {

    private final RoomResponse roomResponse;

    public RoomCreatedEventPayload(RoomResponse dto) {
        super(ChatEventType.ROOM_CREATED);
        this.roomResponse = dto;
    }
}