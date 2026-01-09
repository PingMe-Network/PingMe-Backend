package me.huynhducphu.PingMe_Backend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.advice.base.ErrorCode;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Admin 8/4/2025
 **/
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate =
            new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        delegate.commence(request, response, authException);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

        objectMapper.writeValue(
                response.getWriter(),
                new ApiResponse<>(
                        errorCode.getMessage(),
                        errorCode.getCode()
                )
        );
    }
}
