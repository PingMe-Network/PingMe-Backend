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

    List<MessageResponse> getMessages(Long roomId, String beforeId, int size);

    void appendOlderMessages(Long roomId, List<MessageResponse> messages);

    void evictRoom(Long roomId);

    void updateMessage(Long roomId, String messageId, MessageResponse updated);


}
