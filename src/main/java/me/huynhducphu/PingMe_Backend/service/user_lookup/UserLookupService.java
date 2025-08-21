package me.huynhducphu.PingMe_Backend.service.user_lookup;

import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;

/**
 * Admin 8/21/2025
 **/
public interface UserLookupService {
    UserSummaryResponse lookupUser(String email);
}
