package me.huynhducphu.PingMe_Backend.service.chat.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.service.chat.MessageRedisService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Admin 11/14/2025
 *
 **/
@Service
@RequiredArgsConstructor
public class MessageRedisServiceImpl implements MessageRedisService {

    private static final int MAX_CACHE_MESSAGES = 200;
    private static final Duration CACHE_TTL = Duration.ofHours(2);

    @Qualifier("redisMessageStringTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper redisObjectMapper;

    private String buildKey(Long roomId) {
        return "chat:room:" + roomId + ":messages";
    }

    private void touchTtl(String key) {
        redisTemplate.expire(key, CACHE_TTL);
    }

    @Override
    public void cacheNewMessage(Long roomId, MessageResponse message) {
        String key = buildKey(roomId);
        try {
            String json = redisObjectMapper.writeValueAsString(message);
            redisTemplate.opsForList().leftPush(key, json);
            redisTemplate.opsForList().trim(key, 0, MAX_CACHE_MESSAGES - 1);
            touchTtl(key);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void cacheMessages(Long roomId, List<MessageResponse> messages) {
        if (messages == null || messages.isEmpty()) return;

        String key = buildKey(roomId);

        try {
            List<MessageResponse> copy = new ArrayList<>(messages);
            Collections.reverse(copy);

            for (MessageResponse m : copy) {
                String json = redisObjectMapper.writeValueAsString(m);
                redisTemplate.opsForList().leftPush(key, json);
            }

            redisTemplate.opsForList().trim(key, 0, MAX_CACHE_MESSAGES - 1);
            touchTtl(key);
        } catch (Exception ignored) {
        }
    }

    @Override
    public List<MessageResponse> getLatestMessages(Long roomId, int size) {
        String key = buildKey(roomId);
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, size - 1);
        if (jsonList == null || jsonList.isEmpty()) return List.of();

        List<MessageResponse> result = new ArrayList<>(jsonList.size());
        for (String json : jsonList) {
            try {
                MessageResponse dto = redisObjectMapper.readValue(
                        json,
                        MessageResponse.class
                );
                result.add(dto);
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    @Override
    public void evictRoom(Long roomId) {
        String key = buildKey(roomId);
        redisTemplate.delete(key);
    }

    @Override
    public void updateMessage(Long roomId, Long messageId, MessageResponse updated) {
        String key = buildKey(roomId);
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (jsonList == null || jsonList.isEmpty()) return;

        try {
            String updatedJson = redisObjectMapper.writeValueAsString(updated);

            for (int i = 0; i < jsonList.size(); i++) {
                String json = jsonList.get(i);
                try {
                    MessageResponse dto = redisObjectMapper.readValue(
                            json,
                            MessageResponse.class
                    );
                    if (dto.getId().equals(messageId)) {
                        redisTemplate.opsForList().set(key, i, updatedJson);
                        touchTtl(key);
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void appendOlderMessages(Long roomId, List<MessageResponse> messages) {
        if (messages == null || messages.isEmpty()) return;

        String key = buildKey(roomId);

        try {
            for (MessageResponse m : messages) {
                String json = redisObjectMapper.writeValueAsString(m);
                redisTemplate.opsForList().rightPush(key, json);
            }

            redisTemplate.opsForList().trim(key, 0, MAX_CACHE_MESSAGES - 1);
            touchTtl(key);
        } catch (Exception ignored) {
        }
    }

    @Override
    public List<MessageResponse> getOlderFromCache(Long roomId, Long beforeId, int size) {
        String key = buildKey(roomId);
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (jsonList == null || jsonList.isEmpty()) return List.of();

        List<MessageResponse> all = new ArrayList<>(jsonList.size());
        for (String json : jsonList) {
            try {
                MessageResponse dto = redisObjectMapper.readValue(
                        json,
                        MessageResponse.class
                );
                all.add(dto);
            } catch (Exception ignored) {
            }
        }

        int startIdx = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(beforeId)) {
                startIdx = i;
                break;
            }
        }

        if (startIdx == -1) {
            return List.of();
        }

        int from = startIdx + 1;
        int to = Math.min(from + size, all.size());

        if (from >= all.size()) return List.of();

        return all.subList(from, to);
    }
}