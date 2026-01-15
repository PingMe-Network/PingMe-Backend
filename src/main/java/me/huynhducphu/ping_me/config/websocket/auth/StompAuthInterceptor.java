package me.huynhducphu.ping_me.config.websocket.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * Admin 1/10/2026
 *
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    UserSocketPrincipal userPrincipal = buildUserPrincipal(jwt);

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            Collections.emptyList()
                    );

                    accessor.setUser(auth);

                } catch (Exception e) {
                    throw new AccessDeniedException("Token invalid");
                }
            }
        }
        return message;
    }

    private UserSocketPrincipal buildUserPrincipal(Jwt jwt) {
        UserSocketPrincipal user = new UserSocketPrincipal();
        user.setId(jwt.getClaim("id"));
        user.setEmail(jwt.getSubject());
        user.setUsername(jwt.getClaim("name"));
        return user;
    }
}