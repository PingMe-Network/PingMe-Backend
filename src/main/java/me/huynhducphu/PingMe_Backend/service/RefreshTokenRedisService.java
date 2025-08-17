package me.huynhducphu.PingMe_Backend.service;

import me.huynhducphu.PingMe_Backend.dto.request.auth.SessionMetaRequest;
import me.huynhducphu.PingMe_Backend.dto.response.auth.SessionMetaResponse;

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

    List<SessionMetaResponse> getAllSessionMetas(String userId, String currentRefreshToken);

}
