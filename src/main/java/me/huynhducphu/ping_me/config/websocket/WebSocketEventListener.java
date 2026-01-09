package me.huynhducphu.ping_me.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.ping_me.config.websocket.auth.UserSocketPrincipal;
import me.huynhducphu.ping_me.dto.ws.user_status.UserOnlineStatusRespone;
import me.huynhducphu.ping_me.model.chat.Friendship;
import me.huynhducphu.ping_me.service.authentication.UserAccountService;
import me.huynhducphu.ping_me.service.friendship.FriendshipService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * @author Le Tran Gia Huy
 * created 02/09/2025 - 9:43 PM
 * project PingMe-Backend
 * package me.huynhducphu.PingMe_Backend.config.websocket
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserAccountService userAccountService;
    private final FriendshipService friendshipService;

    @EventListener
    public void handleConnectEvent(SessionConnectedEvent event) {
        var accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (!(principal instanceof UserSocketPrincipal userPrincipal)) {
            log.warn("Không tìm thấy user trong kết nối WebSocket");
            return;
        }

        Long userId = userPrincipal.getId();
        String name = userPrincipal.getName();   // hoặc userPrincipal.getEmail() nếu muốn dùng email

        // Đánh dấu user online
        userAccountService.connect(userId);

        // Lấy danh sách bạn bè
        var friends = friendshipService.getAllFriendshipsOfCurrentUser(userPrincipal.getEmail());

        // Payload gửi cho bạn bè
        var payload = new UserOnlineStatusRespone(userId, name, true);

        // Gửi thông báo online cho từng bạn bè
        for (Friendship f : friends) {
            Long friendId = (f.getUserA().getId().equals(userId))
                    ? f.getUserB().getId()
                    : f.getUserA().getId();

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(friendId), // phải khớp với getName()
                    "/queue/status",
                    payload
            );
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        var accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (!(principal instanceof UserSocketPrincipal userPrincipal)) {
            return;
        }

        Long userId = userPrincipal.getId();
        String name = userPrincipal.getName();

        // Đánh dấu user offline
        userAccountService.disconnect(userId);

        // Lấy danh sách bạn bè
        var friends = friendshipService.getAllFriendshipsOfCurrentUser(userPrincipal.getEmail());

        var payload = new UserOnlineStatusRespone(userId, name, false);


        for (Friendship f : friends) {
            Long friendId = (f.getUserA().getId().equals(userId))
                    ? f.getUserB().getId()
                    : f.getUserA().getId();

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(friendId), // phải khớp với getName()
                    "/queue/status",
                    payload
            );
        }

        log.info("User {} đã offline", name);
    }
}
