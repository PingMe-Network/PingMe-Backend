package me.huynhducphu.PingMe_Backend.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Admin 8/21/2025
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipEvent {
    public enum Type {INVITED, ACCEPTED, REJECTED, CANCELED, DELETED}

    private Type type;
    private Long friendshipId;
    private Long actorId;
    private Long targetId;

}
