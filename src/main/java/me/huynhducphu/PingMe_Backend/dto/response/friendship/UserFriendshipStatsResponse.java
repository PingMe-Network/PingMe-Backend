package me.huynhducphu.PingMe_Backend.dto.response.friendship;

/**
 * Admin 9/2/2025
 *
 **/
public record UserFriendshipStatsResponse(
        Long totalFriends,
        Long totalSentInvites,
        Long totalReceivedInvites
) {
}
