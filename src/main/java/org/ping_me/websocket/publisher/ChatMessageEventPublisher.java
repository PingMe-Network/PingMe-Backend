package org.ping_me.websocket.publisher;

import lombok.RequiredArgsConstructor;
import org.ping_me.dto.response.chat.message.MessageRecalledResponse;
import org.ping_me.service.chat.event.message.MessageCreatedEvent;
import org.ping_me.service.chat.event.message.MessageRecalledEvent;
import org.ping_me.service.chat.event.message.MessageUpdatedEvent;
import org.ping_me.utils.mapper.ChatMapper;
import org.ping_me.websocket.WebSocketDestinations;
import org.ping_me.websocket.dto.chat.message.MessageCreatedEventPayload;
import org.ping_me.websocket.dto.chat.message.MessageRecalledEventPayload;
import org.ping_me.websocket.dto.chat.message.MessageUpdatedEventPayload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatMessageEventPublisher {

    private final RedisWsPublisher redisWsPublisher;
    private final ChatMapper chatMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        var messageResponse = chatMapper.toMessageResponseDto(event.getMessage());
        var roomId = event.getMessage().getRoomId();

        redisWsPublisher.publish(
                WebSocketDestinations.roomMessagesTopic(roomId),
                new MessageCreatedEventPayload(messageResponse)
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageRecalled(MessageRecalledEvent event) {
        var payload = new MessageRecalledEventPayload(
                new MessageRecalledResponse(event.getMessageId())
        );

        redisWsPublisher.publish(
                WebSocketDestinations.roomMessagesTopic(event.getRoomId()),
                payload
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageUpdated(MessageUpdatedEvent event) {
        var messageResponse = chatMapper.toMessageResponseDto(event.getMessage());
        var roomId = event.getMessage().getRoomId();

        redisWsPublisher.publish(
                WebSocketDestinations.roomMessagesTopic(roomId),
                new MessageUpdatedEventPayload(messageResponse)
        );
    }
}
