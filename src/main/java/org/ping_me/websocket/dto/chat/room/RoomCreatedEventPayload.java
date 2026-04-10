package org.ping_me.websocket.dto.chat.room;

import lombok.Getter;
import org.ping_me.dto.response.chat.room.RoomResponse;
import org.ping_me.websocket.dto.chat.common.BaseChatEventPayload;
import org.ping_me.websocket.dto.chat.common.ChatEventType;

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
