package me.huynhducphu.PingMe_Backend.service.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.auth.SessionMetaRequest;
import me.huynhducphu.PingMe_Backend.dto.response.auth.SessionMetaResponse;
import me.huynhducphu.PingMe_Backend.model.common.SessionMeta;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Admin 8/16/2025
 **/
@Service
@RequiredArgsConstructor
public class RefreshTokenRedisServiceImpl implements me.huynhducphu.PingMe_Backend.service.RefreshTokenRedisService {

    private final RedisTemplate<String, SessionMeta> redisSessionMetaTemplate;
    private final ModelMapper modelMapper;

    @Override
    public void saveRefreshToken(
            String token, String userId,
            SessionMetaRequest sessionMetaRequest, Duration expire
    ) {
        String sessionId = buildKey(token, userId);

        SessionMeta sessionMeta = new SessionMeta(
                sessionId,
                sessionMetaRequest.getDeviceType(),
                sessionMetaRequest.getBrowser(),
                sessionMetaRequest.getOs(),
                Instant.now().toString()
        );

        redisSessionMetaTemplate.opsForValue().set(sessionId, sessionMeta, expire);
    }

    @Override
    public List<SessionMetaResponse> getAllSessionMetas(String userId, String currentRefreshToken) {
        String keyPattern = "auth::refresh_token:" + userId + ":*";
        Set<String> keys = redisSessionMetaTemplate.keys(keyPattern);

        if (keys == null || keys.isEmpty()) return Collections.emptyList();
        String currentTokenHash = DigestUtils.sha256Hex(currentRefreshToken);

        List<SessionMetaResponse> sessionMetas = new ArrayList<>();
        for (String key : keys) {
            SessionMeta meta = redisSessionMetaTemplate.opsForValue().get(key);
            if (meta == null) continue;

            String keyHash = key.substring(key.lastIndexOf(":") + 1);
            boolean isCurrent = currentTokenHash.equals(keyHash);

            var sessionMetaResponse = modelMapper.map(meta, SessionMetaResponse.class);
            sessionMetaResponse.setCurrent(isCurrent);

            sessionMetas.add(sessionMetaResponse);
        }
        return sessionMetas;
    }

    @Override
    public void deleteRefreshToken(String token, String userId) {
        redisSessionMetaTemplate.delete(buildKey(token, userId));
    }

    @Override
    public void deleteRefreshToken(String key) {
        redisSessionMetaTemplate.delete(key);
    }

    // =====================================
    // Utilities methods
    // =====================================
    @Override
    public boolean validateToken(String token, String userId) {
        return redisSessionMetaTemplate.hasKey(buildKey(token, userId));
    }

    private String buildKey(String token, String userId) {
        return "auth::refresh_token:" + userId + ":" + DigestUtils.sha256Hex(token);
    }


}
