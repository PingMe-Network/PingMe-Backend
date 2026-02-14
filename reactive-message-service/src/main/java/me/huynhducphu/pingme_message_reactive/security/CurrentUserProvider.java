package me.huynhducphu.pingme_message_reactive.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CurrentUserProvider {

    public Mono<Long> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .map(jwt -> jwt.getClaim("id"))
                .handle((claim, sink) -> {
                    if (claim instanceof Number number) {
                        sink.next(number.longValue());
                        return;
                    }
                    sink.error(new IllegalArgumentException("JWT claim 'id' is missing or invalid"));
                });
    }
}
