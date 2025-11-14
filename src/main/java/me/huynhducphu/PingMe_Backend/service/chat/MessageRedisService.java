package me.huynhducphu.PingMe_Backend.service.chat;

import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;

import java.util.List;

/**
 * Admin 11/14/2025
 *
 **/
public interface MessageRedisService {
    void cacheNewMessage(Long roomId, MessageResponse message);

    void cacheMessages(Long roomId, List<MessageResponse> messages);

    List<MessageResponse> getLatestMessages(Long roomId, int size);

    void evictRoom(Long roomId);

    void updateMessage(Long roomId, Long messageId, MessageResponse updated);

    void appendOlderMessages(Long roomId, List<MessageResponse> messages);

    List<MessageResponse> getOlderFromCache(Long roomId, Long beforeId, int size);
}
