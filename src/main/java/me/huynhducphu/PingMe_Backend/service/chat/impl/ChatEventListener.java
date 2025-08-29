package me.huynhducphu.PingMe_Backend.service.chat.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.MessageCreatedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Admin 8/29/2025
 *
 **/
@Component
@RequiredArgsConstructor
public class ChatEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent ev) {
        String destination = "/topic/rooms/" + ev.getRoomId() + "/messages";
        messagingTemplate.convertAndSend(destination, ev);
    }
}
