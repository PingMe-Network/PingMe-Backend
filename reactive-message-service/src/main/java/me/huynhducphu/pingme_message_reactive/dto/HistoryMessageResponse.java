package me.huynhducphu.pingme_message_reactive.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HistoryMessageResponse(
        List<MessageResponse> messages,
        boolean hasMore,
        String nextBeforeId
) {
}
