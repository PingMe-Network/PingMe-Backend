package me.huynhducphu.ping_me.dto.ws.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.ping_me.dto.response.user.UserSummaryResponse;
import me.huynhducphu.ping_me.dto.ws.friendship.common.FriendshipEventType;

/**
 * Admin 8/30/2025
 *
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipEventPayload {

    private FriendshipEventType type;
    private UserSummaryResponse userSummaryResponse;
}
