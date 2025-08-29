package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;

import java.util.List;

/**
 * Admin 8/29/2025
 *
 **/
@Data
@NoArgsConstructor
public class MessageCreatedEvent {

    private ChatEventType chatEventType = ChatEventType.MESSAGE_CREATED;
    private Long roomId;
    private MessageResponse messageResponse;

    public MessageCreatedEvent(Long roomId, MessageResponse messageResponse) {
        this.roomId = roomId;
        this.messageResponse = messageResponse;
    }
}
