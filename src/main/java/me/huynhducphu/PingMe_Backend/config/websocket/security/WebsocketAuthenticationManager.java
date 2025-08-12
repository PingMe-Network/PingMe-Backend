package me.huynhducphu.PingMe_Backend.config.websocket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

/**
 * Admin 8/11/2025
 **/
@Configuration
public class WebsocketAuthenticationManager {

    @Bean("wsAuthManager")
    public AuthenticationManager wsAuthManager(
            JwtAuthenticationConverter jwtAuthenticationConverter,
            JwtDecoder jwtDecoder
    ) {
        var provider = new JwtAuthenticationProvider(jwtDecoder);
        provider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
        return new ProviderManager(provider);
    }

}
