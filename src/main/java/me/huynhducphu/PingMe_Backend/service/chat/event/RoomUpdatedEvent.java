package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.*;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;

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

}
