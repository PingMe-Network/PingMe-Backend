package org.ping_me.websocket.dto.chat.room;

import lombok.Getter;
import org.ping_me.websocket.dto.chat.common.BaseChatEventPayload;
import org.ping_me.websocket.dto.chat.common.ChatEventType;

/**
 * Admin 4/23/2026
 */
@Getter
public class RoomDeletedEventPayload extends BaseChatEventPayload {

    private final Long roomId;
    private final Long actorUserId;

    public RoomDeletedEventPayload(Long roomId, Long actorUserId) {
        super(ChatEventType.ROOM_DELETED);
        this.roomId = roomId;
        this.actorUserId = actorUserId;
    }
}
