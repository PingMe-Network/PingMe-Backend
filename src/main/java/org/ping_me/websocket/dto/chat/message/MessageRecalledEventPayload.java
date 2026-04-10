package org.ping_me.websocket.dto.chat.message;

import lombok.Getter;
import org.ping_me.dto.response.chat.message.MessageRecalledResponse;
import org.ping_me.websocket.dto.chat.common.BaseChatEventPayload;
import org.ping_me.websocket.dto.chat.common.ChatEventType;

/**
 * Admin 11/3/2025
 *
 **/
@Getter
public class MessageRecalledEventPayload extends BaseChatEventPayload {

    private final MessageRecalledResponse messageRecalledResponse;

    public MessageRecalledEventPayload(MessageRecalledResponse messageRecalledResponse) {
        super(ChatEventType.MESSAGE_RECALLED);
        this.messageRecalledResponse = messageRecalledResponse;
    }
}
