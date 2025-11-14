package me.huynhducphu.PingMe_Backend.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Admin 8/9/2025
 **/
@Component
public class SkipPathBearerTokenResolver implements BearerTokenResolver {

    private final BearerTokenResolver delegate = new DefaultBearerTokenResolver();

    private final List<String> skipPaths = List.of(
            "/auth/logout",
            "/auth/register",
            "/actuator/health",
            "/weather"
    );

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getRequestURI();

        for (String skip : skipPaths) {
            if (path.contains(skip)) {
                return null;
            }
        }

        return delegate.resolve(request);
    }

}
