package me.huynhducphu.PingMe_Backend.service.chat.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class MessageRedisServiceImpl implements MessageRedisService {

    private static final int MAX_CACHE_MESSAGES = 200;
    private static final Duration CACHE_TTL = Duration.ofHours(2);

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper redisObjectMapper;

    public MessageRedisServiceImpl(
            @Qualifier("redisMessageStringTemplate")
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper redisObjectMapper) {
        this.redisTemplate = redisTemplate;
        this.redisObjectMapper = redisObjectMapper;
    }

    /* ========================================================================== */
    /*                             WRITE → REDIS CACHE                             */
    /* ========================================================================== */

    /**
     * Cache 1 tin nhắn mới nhất vào đầu danh sách (newest-first).
     * Giới hạn max bằng MAX_CACHE_MESSAGES và refresh TTL.
     */
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

    /**
     * Cache batch messages (newest → oldest).
     * Dữ liệu đầu vào dạng newest-first nên phải reverse trước khi push.
     * Push vào đầu list → giữ cấu trúc newest-first.
     */
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

    /**
     * Backfill older messages vào cuối list (append older history).
     * Dùng khi scroll lên và DB trả về dữ liệu cũ.
     */
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

    /* ========================================================================== */
    /*                             READ ← REDIS CACHE                              */
    /* ========================================================================== */

    /**
     * Lấy messages từ cache:
     * - beforeId == null → lấy newest batch.
     * - beforeId != null → lọc older hơn beforeId.
     */
    @Override
    public List<MessageResponse> getMessages(Long roomId, Long beforeId, int size) {
        String key = buildKey(roomId);

        List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (jsonList == null || jsonList.isEmpty()) return List.of();

        List<MessageResponse> all = new ArrayList<>(jsonList.size());
        for (String json : jsonList) {
            try {
                all.add(redisObjectMapper.readValue(json, MessageResponse.class));
            } catch (Exception ignored) {
            }
        }

        // case 1: beforeId == null → lấy newest
        if (beforeId == null) {
            int end = Math.min(size, all.size());
            return all.subList(0, end);
        }

        // case 2: beforeId != null → lấy older hơn beforeId
        List<MessageResponse> older = new ArrayList<>(size);
        for (MessageResponse m : all) {
            if (m.getId() < beforeId) {
                older.add(m);
                if (older.size() == size) break;
            }
        }

        return older;
    }

    /* ========================================================================== */
    /*                    UPDATE / REMOVE MESSAGE TRONG CACHE                      */
    /* ========================================================================== */

    /**
     * Xoá toàn bộ cache của room (khi rời nhóm hoặc reload data).
     */
    @Override
    public void evictRoom(Long roomId) {
        String key = buildKey(roomId);
        redisTemplate.delete(key);
    }

    /**
     * Update 1 message trong cache theo messageId.
     * Duyệt list và replace item ngay tại index.
     */
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

    // ========================================================
    // Utils
    // ========================================================
    private String buildKey(Long roomId) {
        return "chat:room:" + roomId + ":messages";
    }

    private void touchTtl(String key) {
        redisTemplate.expire(key, CACHE_TTL);
    }

}