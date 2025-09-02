package me.huynhducphu.PingMe_Backend.dto.ws.friendship.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.FriendshipEventType;
import me.huynhducphu.PingMe_Backend.model.Friendship;

/**
 * Admin 8/21/2025
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipEvent {

    private FriendshipEventType type;
    private Friendship friendship;
    private Long targetId;

}
