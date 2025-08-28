package me.huynhducphu.PingMe_Backend.service.chat;

import me.huynhducphu.PingMe_Backend.dto.request.chat.message.MarkReadRequest;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.ReadStateResponse;

import java.util.List;

/**
 * Admin 8/26/2025
 *
 **/
public interface MessageService {
    MessageResponse sendMessage(SendMessageRequest sendMessageRequest);

    ReadStateResponse markAsRead(MarkReadRequest markReadRequest);

    List<MessageResponse> getHistoryMessages(
            Long roomId, Long beforeId, Integer size
    );
}
