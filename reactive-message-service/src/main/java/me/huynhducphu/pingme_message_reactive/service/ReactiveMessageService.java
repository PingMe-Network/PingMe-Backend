package me.huynhducphu.pingme_message_reactive.service;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.pingme_message_reactive.dto.HistoryMessageResponse;
import me.huynhducphu.pingme_message_reactive.dto.MessageResponse;
import me.huynhducphu.pingme_message_reactive.dto.SendMessageRequest;
import me.huynhducphu.pingme_message_reactive.model.MessageDocument;
import me.huynhducphu.pingme_message_reactive.repository.ReactiveMessageRepository;
import me.huynhducphu.pingme_message_reactive.security.CurrentUserProvider;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactiveMessageService {

    private final ReactiveMessageRepository messageRepository;
    private final CurrentUserProvider currentUserProvider;

    public Mono<MessageResponse> sendMessage(SendMessageRequest request) {
        UUID clientMsgId = UUID.fromString(request.getClientMsgId());

        return currentUserProvider.getCurrentUserId()
                .flatMap(senderId -> messageRepository.findByRoomIdAndSenderIdAndClientMsgId(
                                request.getRoomId(),
                                senderId,
                                clientMsgId
                        )
                        .map(this::toResponse)
                        .switchIfEmpty(
                                messageRepository.save(MessageDocument.builder()
                                                .roomId(request.getRoomId())
                                                .senderId(senderId)
                                                .content(request.getContent())
                                                .type(request.getType())
                                                .clientMsgId(clientMsgId)
                                                .createdAt(LocalDateTime.now())
                                                .recalled(false)
                                                .build())
                                        .map(this::toResponse)
                                        .onErrorResume(DuplicateKeyException.class, ex ->
                                                messageRepository.findByRoomIdAndSenderIdAndClientMsgId(
                                                                request.getRoomId(),
                                                                senderId,
                                                                clientMsgId
                                                        )
                                                        .map(this::toResponse)
                                        )
                        ));
    }

    public Mono<HistoryMessageResponse> getHistory(Long roomId, String beforeId, Integer size) {
        int safeSize = Math.max(1, Math.min(size, 100));
        var pageable = PageRequest.of(0, safeSize + 1);

        Flux<MessageDocument> source = beforeId == null || beforeId.isBlank()
                ? messageRepository.findByRoomIdOrderByIdDesc(roomId, pageable)
                : messageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, beforeId, pageable);

        return source.collectList().map(items -> {
            boolean hasMore = items.size() > safeSize;
            List<MessageDocument> pageItems = hasMore ? items.subList(0, safeSize) : items;
            List<MessageResponse> messages = pageItems.stream()
                    .map(this::toResponse)
                    .toList()
                    .reversed();

            String nextBeforeId = hasMore ? pageItems.get(pageItems.size() - 1).getId() : null;
            return HistoryMessageResponse.builder()
                    .messages(messages)
                    .hasMore(hasMore)
                    .nextBeforeId(nextBeforeId)
                    .build();
        });
    }

    public Mono<MessageResponse> recallMessage(String messageId) {
        return messageRepository.findById(messageId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Message not found")))
                .flatMap(message -> {
                    message.setRecalled(true);
                    return messageRepository.save(message);
                })
                .map(this::toResponse);
    }

    private MessageResponse toResponse(MessageDocument document) {
        return MessageResponse.builder()
                .id(document.getId())
                .roomId(document.getRoomId())
                .senderId(document.getSenderId())
                .content(document.getContent())
                .type(document.getType())
                .clientMsgId(document.getClientMsgId())
                .createdAt(document.getCreatedAt())
                .recalled(document.isRecalled())
                .build();
    }
}
