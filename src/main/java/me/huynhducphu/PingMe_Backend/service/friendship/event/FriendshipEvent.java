package me.huynhducphu.PingMe_Backend.service.friendship.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.common.FriendshipEventType;
import me.huynhducphu.PingMe_Backend.model.chat.Friendship;

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
