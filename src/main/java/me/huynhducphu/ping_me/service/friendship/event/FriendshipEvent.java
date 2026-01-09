package me.huynhducphu.ping_me.service.friendship.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.ping_me.dto.ws.friendship.common.FriendshipEventType;
import me.huynhducphu.ping_me.model.chat.Friendship;

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
