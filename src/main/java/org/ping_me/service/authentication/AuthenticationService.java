package org.ping_me.service.authentication;

import org.ping_me.dto.request.authentication.DefaultLoginRequest;
import org.ping_me.dto.request.authentication.MobileLoginRequest;
import org.ping_me.dto.request.authentication.RegisterRequest;
import org.ping_me.dto.request.authentication.SubmitSessionMetaRequest;
import org.ping_me.dto.response.authentication.AdminLoginResponse;
import org.ping_me.dto.response.authentication.CurrentUserSessionResponse;
import org.ping_me.service.authentication.model.AuthResultWrapper;
import org.springframework.http.ResponseCookie;

/**
 * Admin 8/4/2025
 **/
public interface AuthenticationService {
    CurrentUserSessionResponse register(
            RegisterRequest registerRequest);

    AuthResultWrapper defaultLogin(DefaultLoginRequest defaultLoginRequest);

    AuthResultWrapper mobileLogin(MobileLoginRequest mobileLoginRequest);

    ResponseCookie logout(String refreshToken);

    AuthResultWrapper refreshSession(String refreshToken, SubmitSessionMetaRequest submitSessionMetaRequest);

    AdminLoginResponse adminLogin(DefaultLoginRequest defaultLoginRequest);
}
