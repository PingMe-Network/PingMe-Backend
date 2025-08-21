package me.huynhducphu.PingMe_Backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.user_account.*;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.DefaultAuthResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserDeviceMetaResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserInfoResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.service.user_account.UserAccountService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin 8/4/2025
 **/
@Tag(
        name = "Authentication & User Account",
        description = "Các endpoint phục vụ đăng ký, đăng nhập, xác thực phiên và quản lý tài khoản cá nhân"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserSessionResponse>> registerLocal(
            @RequestBody @Valid RegisterRequest registerRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(userAccountService.register(registerRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> loginLocal(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        var authResultWrapper = userAccountService.login(loginRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(authResultWrapper.getDefaultAuthResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, userAccountService.logout(refreshToken).toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> refreshSession(
            @CookieValue(value = "refresh_token") String refreshToken,
            @RequestBody SessionMetaRequest sessionMetaRequest
    ) {
        var authResultWrapper = userAccountService.refreshSession(refreshToken, sessionMetaRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(authResultWrapper.getDefaultAuthResponse()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSessionResponse>> getCurrentUserSession() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.getCurrentUserSession()));
    }

    @GetMapping("/me/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUserInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.getCurrentUserInfo()));
    }

    @GetMapping("/me/sessions")
    public ResponseEntity<ApiResponse<List<UserDeviceMetaResponse>>> getCurrentUserAllDeviceMetas(
            @CookieValue(value = "refresh_token") String refreshToken
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.getCurrentUserAllDeviceMetas(refreshToken)));
    }

    @PostMapping("/me/password")
    public ResponseEntity<ApiResponse<UserSessionResponse>> updateCurrentUserPassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.updateCurrentUserPassword(changePasswordRequest)));
    }

    @PostMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserSessionResponse>> updateCurrentUserProfile(
            @RequestBody @Valid ChangeProfileRequest changeProfileRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.updateCurrentUserProfile(changeProfileRequest)));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserSessionResponse>> updateCurrentUserAvatar(
            @RequestParam("avatar") MultipartFile avatarFile
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.updateCurrentUserAvatar(avatarFile)));
    }

    @DeleteMapping("/me/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUserDeviceMeta(
            @PathVariable String sessionId
    ) {
        userAccountService.deleteCurrentUserDeviceMeta(sessionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>());
    }

}
