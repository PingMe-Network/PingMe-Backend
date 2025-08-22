package me.huynhducphu.PingMe_Backend.config.websocket;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.config.websocket.auth.CustomHandshakeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Admin 8/10/2025
 **/
@Configuration
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final CustomHandshakeHandler customHandshakeHandler;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // Đăng ký endpoint cho client kết nối STOMP qua WebSocket
                .addEndpoint("/ws")
                .setHandshakeHandler(customHandshakeHandler)
                .setAllowedOrigins(allowedOrigins.split(","))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Các publisher (nơi BE có thể đẩy message xuống FE)
        // /topic/... → broadcast cho nhiều người (public channel).
        // /queue/... → hàng đợi point-to-point hoặc riêng tư (thường kết hợp /user).
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix để Spring map các message riêng theo user
        registry.setUserDestinationPrefix("/user");

        // Prefix khi FE muốn gửi message lên BE (MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");

    }
}
