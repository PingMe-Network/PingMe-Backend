package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.chat.Message;
import me.huynhducphu.PingMe_Backend.model.chat.Room;
import me.huynhducphu.PingMe_Backend.model.chat.RoomParticipant;
import me.huynhducphu.PingMe_Backend.model.constant.RoomRole;

import java.util.List;

/**
 * Admin 11/20/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomMemberRoleChangedEvent {

    private Room room;
    private List<RoomParticipant> participants;
    private Long targetUserId;
    private RoomRole oldRole;
    private RoomRole newRole;
    private Long actorUserId;
    private Message systemMessage;

}
