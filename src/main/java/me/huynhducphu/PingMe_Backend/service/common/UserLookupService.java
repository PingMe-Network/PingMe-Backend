package me.huynhducphu.PingMe_Backend.service.common;

import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummarySimpleResponse;

/**
 * Admin 8/21/2025
 **/
public interface UserLookupService {
    UserSummaryResponse lookupUser(String email);

    UserSummarySimpleResponse lookupUserById(Long userId);
}
