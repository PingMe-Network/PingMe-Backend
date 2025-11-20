package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Getter;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.BaseChatEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.ChatEventType;
import me.huynhducphu.PingMe_Backend.model.constant.RoomRole;

/**
 * Admin 11/20/2025
 *
 **/
@Getter
public class RoomMemberRoleChangedEventPayload extends BaseChatEventPayload {

    private final RoomResponse roomResponse;
    private final Long targetUserId;
    private final RoomRole oldRole;
    private final RoomRole newRole;
    private final Long actorUserId;

    public RoomMemberRoleChangedEventPayload(
            RoomResponse dto,
            Long targetUserId,
            RoomRole oldRole,
            RoomRole newRole,
            Long actorUserId
    ) {
        super(ChatEventType.MEMBER_ROLE_CHANGED);
        this.roomResponse = dto;
        this.targetUserId = targetUserId;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.actorUserId = actorUserId;
    }
}