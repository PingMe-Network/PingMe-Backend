package me.huynhducphu.PingMe_Backend.dto.ws.chat.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.ChatEventType;

/**
 * Admin 11/3/2025
 *
 **/
@Data
@NoArgsConstructor
public class MessageRecalledEventPayload {

    private final ChatEventType chatEventType = ChatEventType.MESSAGE_CREATED;
    private MessageRecalledResponse messageRecalledResponse;

    public MessageRecalledEventPayload(MessageRecalledResponse messageRecalledResponse) {
        this.messageRecalledResponse = messageRecalledResponse;
    }
}
