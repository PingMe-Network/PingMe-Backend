package me.huynhducphu.PingMe_Backend.dto.ws.chat.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.Message;

/**
 * Admin 8/29/2025
 *
 **/
@Data
@NoArgsConstructor
public class MessageCreatedEvent {

    private final ChatEventType chatEventType = ChatEventType.MESSAGE_CREATED;
    private Message message;

    public MessageCreatedEvent(Message message) {
        this.message = message;
    }
}
