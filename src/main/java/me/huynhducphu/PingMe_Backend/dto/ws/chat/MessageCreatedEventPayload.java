package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Getter;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.BaseChatEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.ChatEventType;

/**
 * Admin 8/30/2025
 *
 **/
@Getter
public class MessageCreatedEventPayload extends BaseChatEventPayload {

    private final MessageResponse messageResponse;

    public MessageCreatedEventPayload(MessageResponse messageResponse) {
        super(ChatEventType.MESSAGE_CREATED);
        this.messageResponse = messageResponse;
    }
}
