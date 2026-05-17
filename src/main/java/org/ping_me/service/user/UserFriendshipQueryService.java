package org.ping_me.service.user;

import java.util.List;

/**
 * Query friendship information for user-facing endpoints.
 */
public interface UserFriendshipQueryService {

    boolean isFriend(Long hostUserId, Long userId);

    List<Long> getFriendIds(Long userId);
}

