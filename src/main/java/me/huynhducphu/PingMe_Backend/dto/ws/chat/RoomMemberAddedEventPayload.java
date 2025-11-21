package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Getter;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.BaseChatEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.ChatEventType;
import me.huynhducphu.PingMe_Backend.model.Message;

/**
 * Admin 11/20/2025
 *
 **/
@Getter
public class RoomMemberAddedEventPayload extends BaseChatEventPayload {

    private final RoomResponse roomResponse;
    private final Long targetUserId;
    private final Long actorUserId;
    private MessageResponse systemMessage;

    public RoomMemberAddedEventPayload(
            RoomResponse dto,
            Long targetUserId,
            Long actorUserId,
            MessageResponse systemMessage
    ) {
        super(ChatEventType.MEMBER_ADDED);
        this.roomResponse = dto;
        this.targetUserId = targetUserId;
        this.actorUserId = actorUserId;
        this.systemMessage = systemMessage;
    }

}
