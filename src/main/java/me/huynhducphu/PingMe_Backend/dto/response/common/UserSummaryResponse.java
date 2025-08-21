package me.huynhducphu.PingMe_Backend.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.FriendshipStatus;

/**
 * Admin 8/19/2025
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryResponse {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private FriendshipSummary friendshipSummary;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FriendshipSummary {
        private Long id;
        private FriendshipStatus friendshipStatus;
    }

}
