package me.huynhducphu.PingMe_Backend.dto.ws.chat;

import lombok.Getter;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.BaseChatEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.common.ChatEventType;

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
