package me.huynhducphu.PingMe_Backend.service.user_account;

import me.huynhducphu.PingMe_Backend.dto.request.user_account.*;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.common.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserDeviceMetaResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserInfoResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserSessionResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin 8/4/2025
 **/
public interface UserAccountService {
    UserSessionResponse register(
            RegisterRequest registerRequest);

    AuthResultWrapper login(LoginRequest loginRequest);

    ResponseCookie logout(String refreshToken);

    AuthResultWrapper refreshSession(String refreshToken, SessionMetaRequest sessionMetaRequest);

    void deleteCurrentUserDeviceMeta(String sessionId);

    UserSessionResponse getCurrentUserSession();

    UserInfoResponse getCurrentUserInfo();

    List<UserDeviceMetaResponse> getCurrentUserAllDeviceMetas(
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
