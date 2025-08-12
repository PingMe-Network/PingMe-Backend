package me.huynhducphu.PingMe_Backend.config.websocket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Admin 8/12/2025
 **/
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final @Qualifier("wsAuthManager") AuthenticationManager wsAuthManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");
            if (accessToken == null) throw new AccessDeniedException("Missing token");

            wsAuthManager.authenticate(new BearerTokenAuthenticationToken(accessToken.trim()));
        }
        return message;
    }
}

