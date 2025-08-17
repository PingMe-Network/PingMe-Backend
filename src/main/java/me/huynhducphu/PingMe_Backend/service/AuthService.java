package me.huynhducphu.PingMe_Backend.service;

import me.huynhducphu.PingMe_Backend.dto.request.auth.*;
import me.huynhducphu.PingMe_Backend.dto.common.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.auth.SessionMetaResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserDetailResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import org.springframework.http.ResponseCookie;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin 8/4/2025
 **/
public interface AuthService {
    UserSessionResponse register(
            RegisterRequest registerRequest);

    AuthResultWrapper login(LoginRequest loginRequest);

    ResponseCookie logout(String refreshToken);

    AuthResultWrapper refreshSession(String refreshToken, SessionMetaRequest sessionMetaRequest);

    void deleteCurrentUserSession(String sessionId);

    User getCurrentUser();

    UserSessionResponse getCurrentUserSession();

    UserDetailResponse getCurrentUserDetail();

    List<SessionMetaResponse> getCurrentUserSessions(
            String refreshToken
    );

    UserSessionResponse updateCurrentUserPassword(
            ChangePasswordRequest changePasswordRequest
    );

    UserSessionResponse updateCurrentUserProfile(
            ChangeProfileRequest changeProfileRequest
    );

    UserSessionResponse updateCurrentUserAvatar(
            MultipartFile avatarFile
    );
}
