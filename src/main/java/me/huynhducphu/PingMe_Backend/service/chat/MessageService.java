package me.huynhducphu.PingMe_Backend.service.chat;

import me.huynhducphu.PingMe_Backend.dto.request.chat.message.MarkReadRequest;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.HistoryMessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.ReadStateResponse;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin 8/26/2025
 *
 **/
public interface MessageService {
    MessageResponse sendMessage(SendMessageRequest sendMessageRequest);

    MessageResponse sendFileMessage(
            SendMessageRequest sendMessageRequest,
            MultipartFile file
    );

    MessageRecalledResponse recallMessage(Long messageId);

    ReadStateResponse markAsRead(MarkReadRequest markReadRequest);

    HistoryMessageResponse getHistoryMessages(
            Long roomId, Long beforeId, Integer size
    );

    Message createSystemMessage(Room room, String content, User user);
}
