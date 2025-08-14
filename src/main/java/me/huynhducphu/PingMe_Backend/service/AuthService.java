package me.huynhducphu.PingMe_Backend.service;

import me.huynhducphu.PingMe_Backend.dto.request.auth.ChangePasswordRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.ChangeProfileRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.LocalLoginRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.RegisterLocalRequest;
import me.huynhducphu.PingMe_Backend.dto.response.auth.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserDetailResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import org.springframework.http.ResponseCookie;

/**
 * Admin 8/4/2025
 **/
public interface AuthService {
    UserSessionResponse registerLocal(
            RegisterLocalRequest registerLocalRequest);

    AuthResultWrapper loginLocal(LocalLoginRequest localLoginRequest);

    ResponseCookie logout();

    AuthResultWrapper refreshSession(String refreshToken);

    User getCurrentUser();

    UserSessionResponse getCurrentUserSession();

    UserDetailResponse getCurrentUserDetail();

    UserSessionResponse updateCurrentUserPassword(
            ChangePasswordRequest changePasswordRequest
    );

    UserSessionResponse updateCurrentUserProfile(
            ChangeProfileRequest changeProfileRequest
    );
}
