package me.huynhducphu.ping_me.service.authentication;

import me.huynhducphu.ping_me.dto.request.authentication.*;
import me.huynhducphu.ping_me.dto.response.authentication.common.AuthResultWrapper;
import me.huynhducphu.ping_me.dto.response.authentication.CurrentUserSessionResponse;
import org.springframework.http.ResponseCookie;

/**
 * Admin 8/4/2025
 **/
public interface AuthenticationService {
    CurrentUserSessionResponse register(
            RegisterRequest registerRequest);

    AuthResultWrapper login(LoginRequest loginRequest);

    ResponseCookie logout(String refreshToken);

    AuthResultWrapper refreshSession(String refreshToken, SubmitSessionMetaRequest submitSessionMetaRequest);
}
