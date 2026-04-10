package org.ping_me.websocket.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ping_me.dto.response.user.UserSummaryResponse;
import org.ping_me.websocket.dto.friendship.common.FriendshipEventType;

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
