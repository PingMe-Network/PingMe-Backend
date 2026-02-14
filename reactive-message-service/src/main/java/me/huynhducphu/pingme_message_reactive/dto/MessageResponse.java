package me.huynhducphu.pingme_message_reactive.dto;

import lombok.Builder;
import me.huynhducphu.pingme_message_reactive.model.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MessageResponse(
        String id,
        Long roomId,
        Long senderId,
        String content,
        MessageType type,
        UUID clientMsgId,
        LocalDateTime createdAt,
        boolean recalled
) {
}
