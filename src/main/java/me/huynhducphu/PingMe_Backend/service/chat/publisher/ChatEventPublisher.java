package me.huynhducphu.PingMe_Backend.service.chat.publisher;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.MessageCreatedEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.MessageRecalledEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.RoomUpdatedEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.payload.MessageCreatedEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.payload.MessageRecalledEventPayload;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.payload.RoomUpdatedEventPayload;
import me.huynhducphu.PingMe_Backend.service.chat.util.ChatDtoUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * Admin 8/29/2025
 *
 **/
@Component
@RequiredArgsConstructor
public class ChatEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        var messageResponse = ChatDtoUtils.toMessageResponseDto(event.getMessage());
        var roomId = event.getMessage().getRoom().getId();

        String destination = "/topic/rooms/" + roomId + "/messages";
        var payload = new MessageCreatedEventPayload(messageResponse);

        messagingTemplate.convertAndSend(destination, payload);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageRecalled(MessageRecalledEvent event) {
        var roomId = event.getRoomId();
        var messageRecalledResponse = new MessageRecalledResponse(event.getMessageId());

        String destination = "/topic/rooms/" + roomId + "/messages";
        var payload = new MessageRecalledEventPayload(messageRecalledResponse);

        messagingTemplate.convertAndSend(destination, payload);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoomUpdated(RoomUpdatedEvent event) {
        List<Long> participantIds = event
                .getRoomParticipants()
                .stream()
                .map(x -> x.getUser().getId())
                .toList();

        for (Long userId : participantIds) {
            var payload = new RoomUpdatedEventPayload(ChatDtoUtils.toRoomResponseDto(
                    event.getRoom(),
                    event.getRoomParticipants(),
                    userId
            ));

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/rooms",
                    payload
            );
        }
    }
}
