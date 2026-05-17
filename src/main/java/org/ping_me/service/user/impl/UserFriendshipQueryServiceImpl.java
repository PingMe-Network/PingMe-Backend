package org.ping_me.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.ping_me.model.chat.Friendship;
import org.ping_me.model.constant.FriendshipStatus;
import org.ping_me.repository.jpa.chat.FriendshipRepository;
import org.ping_me.service.user.UserFriendshipQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Query service for friendship lookup endpoints.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFriendshipQueryServiceImpl implements UserFriendshipQueryService {

    private final FriendshipRepository friendshipRepository;

    @Override
    public boolean isFriend(Long hostUserId, Long userId) {
        if (hostUserId == null || userId == null) {
            return false;
        }

        Long low = Math.min(hostUserId, userId);
        Long high = Math.max(hostUserId, userId);

        return friendshipRepository.findByUserLowIdAndUserHighId(low, high)
                .map(Friendship::getFriendshipStatus)
                .filter(status -> status == FriendshipStatus.ACCEPTED)
                .isPresent();
    }

    @Override
    public List<Long> getFriendIds(Long userId) {
        if (userId == null) {
            return List.of();
        }

        return friendshipRepository.findAllByStatusAndUserWithoutBeforeId(
                        FriendshipStatus.ACCEPTED,
                        userId
                )
                .stream()
                .map(friendship -> friendship.getUserLowId().equals(userId)
                        ? friendship.getUserHighId()
                        : friendship.getUserLowId())
                .toList();
    }
}

