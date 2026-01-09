package me.huynhducphu.ping_me.service.common;

import me.huynhducphu.ping_me.dto.response.user.UserSummaryResponse;
import me.huynhducphu.ping_me.dto.response.user.UserSummarySimpleResponse;

/**
 * Admin 8/21/2025
 **/
public interface UserLookupService {
    UserSummaryResponse lookupUser(String email);
    UserSummarySimpleResponse lookupUserById(Long userId);
}
