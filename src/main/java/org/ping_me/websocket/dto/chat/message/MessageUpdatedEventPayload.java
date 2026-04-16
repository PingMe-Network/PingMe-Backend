package org.ping_me.websocket.dto.chat.message;

import lombok.Getter;
import org.ping_me.dto.response.chat.message.MessageResponse;
import org.ping_me.websocket.dto.chat.common.BaseChatEventPayload;
import org.ping_me.websocket.dto.chat.common.ChatEventType;

/**
 * Admin 4/16/2026
 */
@Getter
public class MessageUpdatedEventPayload extends BaseChatEventPayload {

    private final MessageResponse messageResponse;

    public MessageUpdatedEventPayload(MessageResponse messageResponse) {
        super(ChatEventType.MESSAGE_UPDATED);
        this.messageResponse = messageResponse;
    }
}
