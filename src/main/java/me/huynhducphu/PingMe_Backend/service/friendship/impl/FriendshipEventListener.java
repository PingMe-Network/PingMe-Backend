package me.huynhducphu.PingMe_Backend.service.friendship.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.FriendshipEvent;
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

        switch (event.getType()) {
            case ACCEPTED, REJECTED,
                 CANCELED, DELETED,
                 INVITED -> messagingTemplate.convertAndSendToUser(
                    event.getTargetId().toString(),
                    "/queue/friendship",
                    event
            );
        }
    }

}
