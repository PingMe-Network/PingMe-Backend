package me.huynhducphu.PingMe_Backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.auth.ChangePasswordRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.ChangeProfileRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.LocalLoginRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.RegisterLocalRequest;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.DefaultAuthResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserDetailResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.service.AuthService;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/4/2025
 **/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserSessionResponse>> registerLocal(
            @RequestBody @Valid RegisterLocalRequest registerLocalRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(authService.registerLocal(registerLocalRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> loginLocal(
            @RequestBody @Valid LocalLoginRequest localLoginRequest
    ) {
        var authResultWrapper = authService.loginLocal(localLoginRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(authResultWrapper.getDefaultAuthResponse()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authService.logout().toString())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> refreshSession(
            @CookieValue(value = "refresh_token") String refreshToken
    ) {
        var authResultWrapper = authService.refreshSession(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(authResultWrapper.getDefaultAuthResponse()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSessionResponse>> getCurrentUserSession() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(authService.getCurrentUserSession()));
    }

    @GetMapping("/me/detail")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getCurrentUserDetail() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(authService.getCurrentUserDetail()));
    }

    @PostMapping("/me/password")
    public ResponseEntity<ApiResponse<UserSessionResponse>> updateCurrentUserPassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(authService.updateCurrentUserPassword(changePasswordRequest)));
    }

    @PostMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserSessionResponse>> updateCurrentUserProfile(
            @RequestBody @Valid ChangeProfileRequest changeProfileRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(authService.updateCurrentUserProfile(changeProfileRequest)));
    }

}
