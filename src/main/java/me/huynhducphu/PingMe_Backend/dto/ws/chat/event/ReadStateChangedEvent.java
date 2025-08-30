package me.huynhducphu.PingMe_Backend.dto.ws.chat.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.ChatEventType;

import java.time.LocalDateTime;

/**
 * Admin 8/29/2025
 *
 **/
@Data
@NoArgsConstructor
public class ReadStateChangedEvent {

    private ChatEventType chatEventType = ChatEventType.READ_STATE_CHANGED;
    private Long id;
    private Long roomId;
    private Long lastReadMessageId;
    private LocalDateTime lastReadAt;

    public ReadStateChangedEvent(Long id, Long lastReadMessageId, LocalDateTime lastReadAt, Long roomId) {
        this.id = id;
        this.lastReadMessageId = lastReadMessageId;
        this.lastReadAt = lastReadAt;
        this.roomId = roomId;
    }
}
