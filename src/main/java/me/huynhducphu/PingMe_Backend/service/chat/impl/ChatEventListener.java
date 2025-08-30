package me.huynhducphu.PingMe_Backend.service.chat.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.MessageCreatedEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.RoomUpdatedEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.payload.MessageCreatedEventPayload;
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
public class ChatEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatDtoUtils chatDtoUtils;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        var messageResponse = chatDtoUtils.toMessageResponseDto(event.getMessage());
        long roomId = messageResponse.getRoomId();

        String destination = "/topic/rooms/" + roomId + "/messages";
        var payload = new MessageCreatedEventPayload(messageResponse);
        
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
            var payload = new RoomUpdatedEventPayload(chatDtoUtils.toRoomResponseDto(
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
