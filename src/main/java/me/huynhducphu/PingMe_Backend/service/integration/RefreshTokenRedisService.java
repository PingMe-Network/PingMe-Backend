package me.huynhducphu.PingMe_Backend.service.integration;

import me.huynhducphu.PingMe_Backend.dto.request.user_account.SessionMetaRequest;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserDeviceMetaResponse;

import java.time.Duration;
import java.util.List;

/**
 * Admin 8/16/2025
 **/
public interface RefreshTokenRedisService {
    void saveRefreshToken(
            String token, String userId,
            SessionMetaRequest sessionMetaRequest, Duration expire
    );

    boolean validateToken(String token, String userId);

    void deleteRefreshToken(String token, String userId);

    void deleteRefreshToken(String key);

    List<UserDeviceMetaResponse> getAllDeviceMetas(String userId, String currentRefreshToken);

}
