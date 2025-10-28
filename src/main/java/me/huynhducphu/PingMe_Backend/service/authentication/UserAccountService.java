package me.huynhducphu.PingMe_Backend.service.authentication;

import me.huynhducphu.PingMe_Backend.dto.request.authentication.*;
import me.huynhducphu.PingMe_Backend.dto.response.authentication.common.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.authentication.CurrentUserDeviceMetaResponse;
import me.huynhducphu.PingMe_Backend.dto.response.authentication.CurrentUserProfileResponse;
import me.huynhducphu.PingMe_Backend.dto.response.authentication.CurrentUserSessionResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin 8/4/2025
 **/
public interface UserAccountService {
    CurrentUserSessionResponse register(
            RegisterRequest registerRequest);

    AuthResultWrapper login(LoginRequest loginRequest);

    ResponseCookie logout(String refreshToken);

    AuthResultWrapper refreshSession(String refreshToken, SubmitSessionMetaRequest submitSessionMetaRequest);

    void deleteCurrentUserDeviceMeta(String sessionId);

    CurrentUserSessionResponse getCurrentUserSession();

    CurrentUserProfileResponse getCurrentUserInfo();

    List<CurrentUserDeviceMetaResponse> getCurrentUserAllDeviceMetas(
            String refreshToken
    );

    CurrentUserSessionResponse updateCurrentUserPassword(
            ChangePasswordRequest changePasswordRequest
    );

    CurrentUserSessionResponse updateCurrentUserProfile(
            ChangeProfileRequest changeProfileRequest
    );

    CurrentUserSessionResponse updateCurrentUserAvatar(
            MultipartFile avatarFile
    );

    void connect(Long userId);

    void disconnect(Long userId);
}
