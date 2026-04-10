package org.ping_me.websocket.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.ping_me.websocket.auth.UserSocketPrincipal;
import org.ping_me.websocket.WebSocketDestinations;
import org.ping_me.websocket.dto.chat.message.MessageTypingEventPayload;
import org.ping_me.websocket.publisher.RedisWsPublisher;
import org.ping_me.service.chat.event.message.MessageTypingEvent;
import org.ping_me.utils.mapper.UserMapper;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Admin 2/18/2026
 *
 **/
@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatTypingController {

    RedisWsPublisher redisWsPublisher;
    UserMapper userMapper;

    /* ========================================================================== */
    /*                           MESSAGE TYPING                                   */
    /* ========================================================================== */
    public ChatTypingController(
            RedisWsPublisher redisWsPublisher,
            UserMapper userMapper
    ) {
        this.redisWsPublisher = redisWsPublisher;
        this.userMapper = userMapper;
    }

    @MessageMapping("/rooms/{roomId}/typing")
    public void handleTypingSignal(
            @DestinationVariable Long roomId,
            @Payload MessageTypingEvent payload,
            Principal principal
    ) {
        UserSocketPrincipal user = userMapper.extractUserPrincipal(principal);
        if (user == null) return;

        var signal = new MessageTypingEventPayload(
                roomId,
                user.getId(),
                user.getUsername(),
                payload.isTyping()
        );

        redisWsPublisher.publish(WebSocketDestinations.roomTypingTopic(roomId), signal);
    }
}
