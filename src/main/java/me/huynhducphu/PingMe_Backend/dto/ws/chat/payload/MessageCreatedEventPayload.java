package me.huynhducphu.PingMe_Backend.dto.ws.chat.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.ChatEventType;

/**
 * Admin 8/30/2025
 *
 **/
@Data
@NoArgsConstructor
public class MessageCreatedEventPayload {

    private final ChatEventType chatEventType = ChatEventType.MESSAGE_CREATED;
    private MessageResponse messageResponse;

    public MessageCreatedEventPayload(MessageResponse messageResponse) {
        this.messageResponse = messageResponse;
    }
}
