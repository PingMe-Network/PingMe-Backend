package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.*;
import me.huynhducphu.PingMe_Backend.model.chat.Message;
import me.huynhducphu.PingMe_Backend.model.chat.Room;
import me.huynhducphu.PingMe_Backend.model.chat.RoomParticipant;

import java.util.List;

/**
 * Admin 8/29/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomUpdatedEvent {

    private Room room;
    private List<RoomParticipant> roomParticipants;
    private Message systemMessage;

}
