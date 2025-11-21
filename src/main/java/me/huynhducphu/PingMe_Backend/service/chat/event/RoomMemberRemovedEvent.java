package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;

import java.util.List;

/**
 * Admin 11/20/2025
 * \
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomMemberRemovedEvent {

    private Room room;
    private List<RoomParticipant> participants;
    private Long targetUserId;
    private Long actorUserId;
    private Message systemMessage;

}
