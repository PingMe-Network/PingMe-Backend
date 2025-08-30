package me.huynhducphu.PingMe_Backend.dto.ws.chat.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.ChatEventType;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;

import java.util.List;

/**
 * Admin 8/29/2025
 *
 **/
@Data
@NoArgsConstructor
public class RoomUpdatedEvent {

    private ChatEventType chatEventType = ChatEventType.ROOM_UPDATED;
    private Room room;
    private List<RoomParticipant> roomParticipants;

    public RoomUpdatedEvent(Room room, List<RoomParticipant> roomParticipants) {
        this.room = room;
        this.roomParticipants = roomParticipants;
    }
}
