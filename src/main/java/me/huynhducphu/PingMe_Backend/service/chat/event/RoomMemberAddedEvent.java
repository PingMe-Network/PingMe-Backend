package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.chat.Message;
import me.huynhducphu.PingMe_Backend.model.chat.Room;
import me.huynhducphu.PingMe_Backend.model.chat.RoomParticipant;

import java.util.List;

/**
 * Admin 11/20/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomMemberAddedEvent {

    private Room room;
    private List<RoomParticipant> roomParticipants;
    private Long targetUserId;
    private Long actorUserId;
    private Message systemMessage;


}
