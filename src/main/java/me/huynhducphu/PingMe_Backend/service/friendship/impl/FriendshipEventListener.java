package me.huynhducphu.PingMe_Backend.service.friendship.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.event.FriendshipEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.payload.FriendshipEventPayload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Admin 8/21/2025
 **/
@Component
@RequiredArgsConstructor
public class FriendshipEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFriendshipEvent(FriendshipEvent event) {
        var payload = new FriendshipEventPayload(event.getType(), event.getFriendshipId());

        switch (event.getType()) {
            case ACCEPTED, REJECTED,
                 CANCELED, DELETED,
                 INVITED -> messagingTemplate.convertAndSendToUser(
                    event.getTargetId().toString(),
                    "/queue/friendship",
                    payload
            );
        }
    }

}
