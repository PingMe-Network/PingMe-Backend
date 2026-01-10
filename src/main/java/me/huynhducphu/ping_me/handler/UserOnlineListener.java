package me.huynhducphu.ping_me.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.ping_me.config.websocket.auth.UserSocketPrincipal;
import me.huynhducphu.ping_me.dto.ws.user_status.UserOnlineStatusRespone;
import me.huynhducphu.ping_me.model.chat.Friendship;
import me.huynhducphu.ping_me.service.friendship.FriendshipService;
import me.huynhducphu.ping_me.service.user.CurrentUserProfileService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication; // Import thêm cái này
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserOnlineListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final CurrentUserProfileService currentUserProfileService;
    private final FriendshipService friendshipService;

    @EventListener
    public void handleConnectEvent(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        UserSocketPrincipal userPrincipal = extractUserPrincipal(accessor.getUser());

        if (userPrincipal == null) {
            return;
        }

        Long userId = userPrincipal.getId();
        String name = userPrincipal.getName();

        currentUserProfileService.connect(userId);
        var friends = friendshipService.getAllFriendshipsOfCurrentUser(userPrincipal.getEmail());
        var payload = new UserOnlineStatusRespone(userId, name, true);

        for (Friendship f : friends) {
            Long friendId = (f.getUserA().getId().equals(userId)) ? f.getUserB().getId() : f.getUserA().getId();
            messagingTemplate.convertAndSendToUser(String.valueOf(friendId), "/queue/status", payload);
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        UserSocketPrincipal userPrincipal = extractUserPrincipal(accessor.getUser());

        if (userPrincipal == null) {
            return;
        }

        Long userId = userPrincipal.getId();
        String name = userPrincipal.getName();

        currentUserProfileService.disconnect(userId);
        var friends = friendshipService.getAllFriendshipsOfCurrentUser(userPrincipal.getEmail());
        var payload = new UserOnlineStatusRespone(userId, name, false);

        for (Friendship f : friends) {
            Long friendId = (f.getUserA().getId().equals(userId)) ? f.getUserB().getId() : f.getUserA().getId();
            messagingTemplate.convertAndSendToUser(String.valueOf(friendId), "/queue/status", payload);
        }
    }

    private UserSocketPrincipal extractUserPrincipal(Principal principal) {
        if (principal instanceof Authentication auth) {
            Object receivedPrincipal = auth.getPrincipal();
            if (receivedPrincipal instanceof UserSocketPrincipal) {
                return (UserSocketPrincipal) receivedPrincipal;
            }
        }

        if (principal instanceof UserSocketPrincipal) {
            return (UserSocketPrincipal) principal;
        }
        return null;
    }
}