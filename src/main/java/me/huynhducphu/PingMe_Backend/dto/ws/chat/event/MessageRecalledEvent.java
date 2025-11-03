package me.huynhducphu.PingMe_Backend.dto.ws.chat.event;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 11/3/2025
 *
 **/
@Data
@NoArgsConstructor
public class MessageRecalledEvent {

    private final ChatEventType chatEventType = ChatEventType.MESSAGE_RECALLED;
    private Long messageId;
    private Long roomId;

    public MessageRecalledEvent(Long messageId, Long roomId) {
        this.messageId = messageId;
        this.roomId = roomId;
    }
}
