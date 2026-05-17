package org.ping_me.controller.user;

import lombok.RequiredArgsConstructor;
import org.ping_me.service.user.UserFriendshipQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple query API used by other services to check friendship between two
 * users.
 */
@RestController
@RequestMapping("/core-service/users")
@RequiredArgsConstructor
public class UserFriendshipQueryController {

    private final UserFriendshipQueryService userFriendshipQueryService;

    @GetMapping("/{hostUserId}/is-friend/{userId}")
    public ResponseEntity<Boolean> isFriend(
            @PathVariable Long hostUserId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(userFriendshipQueryService.isFriend(hostUserId, userId));
    }

    @GetMapping("/{userId}/friend-ids")
    public ResponseEntity<java.util.List<Long>> getFriendIds(@PathVariable Long userId) {
        return ResponseEntity.ok(userFriendshipQueryService.getFriendIds(userId));
    }
}
