package me.huynhducphu.PingMe_Backend.config.websocket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


/**
 * Admin 8/12/2025
 **/
@Component
@RequiredArgsConstructor
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    private final AuthenticationManager wsAuthManager;
    private final JwtDecoder jwtDecoder;

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String accessToken = extractAccessToken(request);

        if (StringUtils.hasText(accessToken)) {
            authenticateUser(accessToken);
            return extractUser(accessToken);
        }

        throw new AccessDeniedException("JWT không tồn tại");
    }

    private String extractAccessToken(ServerHttpRequest request) {
        return Optional.ofNullable(request.getURI().getQuery())
                .map(query -> query.split("&"))
                .flatMap(params ->
                        Arrays.stream(params)
                                .filter(param -> param.startsWith("access_token="))
                                .findFirst()
                )
                .map(param -> param.split("=")[1])
                .orElse(null);
    }

    private UserSocketPrincipal extractUser(String accessToken) {
        var userSocketPrincipal = new UserSocketPrincipal();

        var jwt = jwtDecoder.decode(accessToken);
        Map<String, Object> claims = jwt.getClaim("user");

        userSocketPrincipal.setEmail((String) claims.get("email"));
        userSocketPrincipal.setName((String) claims.get("name"));
        return userSocketPrincipal;
    }

    private Principal authenticateUser(String accessToken) {
        try {
            BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(accessToken);
            Authentication authentication = wsAuthManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (Exception e) {
            throw new AccessDeniedException("JWT không hợp lệ");
        }
    }
}
