package me.huynhducphu.ping_me.controller.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.base.ApiResponse;
import me.huynhducphu.ping_me.dto.request.authentication.*;
import me.huynhducphu.ping_me.dto.response.authentication.*;
import me.huynhducphu.ping_me.dto.response.authentication.auth.DefaultAuthResponse;
import me.huynhducphu.ping_me.dto.response.authentication.auth.MobileAuthResponse;
import me.huynhducphu.ping_me.service.authentication.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/4/2025
 **/
@Tag(
        name = "Default Authentication",
        description = "Các API đăng ký, đăng nhập, xác thực phiên và quản lý tài khoản người dùng"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class DefaultAuthenticationController {

    private final AuthenticationService authenticationService;

    // ================= REGISTER =================
    @Operation(
            summary = "Đăng ký tài khoản",
            description = "Đăng ký tài khoản người dùng mới bằng email và mật khẩu"
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CurrentUserSessionResponse>> registerLocal(
            @Parameter(description = "Thông tin đăng ký tài khoản", required = true)
            @RequestBody @Valid RegisterRequest registerRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(authenticationService.register(registerRequest)));
    }

    // ================= LOGIN =================
    @Operation(
            summary = "Đăng nhập",
            description = "Đăng nhập và khởi tạo phiên làm việc mới"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> login(
            @Parameter(description = "Thông tin đăng nhập", required = true)
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        var authResultWrapper = authenticationService.login(loginRequest);
        var payload = new DefaultAuthResponse(
                authResultWrapper.getUserSession(),
                authResultWrapper.getAccessToken()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(payload));
    }

    // ================= LOGOUT =================
    @Operation(
            summary = "Đăng xuất",
            description = "Đăng xuất và hủy refresh token hiện tại"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Refresh token từ cookie")
            @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, authenticationService.logout(refreshToken).toString())
                .build();
    }

    // ================= REFRESH =================
    @Operation(
            summary = "Làm mới phiên",
            description = "Làm mới access token bằng refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> refreshSession(
            @Parameter(description = "Refresh token từ cookie", required = true)
            @CookieValue(value = "refresh_token") String refreshToken,

            @Parameter(description = "Thông tin thiết bị và session")
            @RequestBody SubmitSessionMetaRequest submitSessionMetaRequest
    ) {
        var authResultWrapper = authenticationService.refreshSession(refreshToken, submitSessionMetaRequest);
        var payload = new DefaultAuthResponse(
                authResultWrapper.getUserSession(),
                authResultWrapper.getAccessToken()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(payload));
    }
}
