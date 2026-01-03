package me.huynhducphu.PingMe_Backend.controller.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.request.authentication.*;
import me.huynhducphu.PingMe_Backend.dto.response.authentication.*;
import me.huynhducphu.PingMe_Backend.service.authentication.UserAccountService;
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
        description = "Các API đăng ký, đăng nhập, xác thực phiên và quản lý tài khoản người dùng"
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

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
                .body(new ApiResponse<>(userAccountService.register(registerRequest)));
    }

    // ================= LOGIN =================
    @Operation(
            summary = "Đăng nhập",
            description = "Đăng nhập và khởi tạo phiên làm việc mới"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> loginLocal(
            @Parameter(description = "Thông tin đăng nhập", required = true)
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        var authResultWrapper = userAccountService.login(loginRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(authResultWrapper.getDefaultAuthResponse()));
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
                .header(HttpHeaders.SET_COOKIE, userAccountService.logout(refreshToken).toString())
                .build();
    }

    // ================= REFRESH =================
    @Operation(
            summary = "Refresh session",
            description = "Làm mới access token bằng refresh token"
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<DefaultAuthResponse>> refreshSession(
            @Parameter(description = "Refresh token từ cookie", required = true)
            @CookieValue(value = "refresh_token") String refreshToken,

            @Parameter(description = "Thông tin thiết bị và session")
            @RequestBody SubmitSessionMetaRequest submitSessionMetaRequest
    ) {
        var authResultWrapper = userAccountService.refreshSession(refreshToken, submitSessionMetaRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authResultWrapper.getRefreshTokenCookie().toString())
                .body(new ApiResponse<>(authResultWrapper.getDefaultAuthResponse()));
    }

    // ================= CURRENT SESSION =================
    @Operation(
            summary = "Phiên người dùng hiện tại",
            description = "Lấy thông tin session của người dùng đang đăng nhập"
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserSessionResponse>> getCurrentUserSession() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.getCurrentUserSession()));
    }

    // ================= PROFILE =================
    @Operation(
            summary = "Thông tin người dùng",
            description = "Lấy thông tin hồ sơ người dùng hiện tại"
    )
    @GetMapping("/me/info")
    public ResponseEntity<ApiResponse<CurrentUserProfileResponse>> getCurrentUserInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.getCurrentUserInfo()));
    }

    // ================= SESSIONS =================
    @Operation(
            summary = "Danh sách thiết bị đăng nhập",
            description = "Lấy danh sách các thiết bị đã đăng nhập của người dùng"
    )
    @GetMapping("/me/sessions")
    public ResponseEntity<ApiResponse<List<CurrentUserDeviceMetaResponse>>> getCurrentUserAllDeviceMetas(
            @Parameter(description = "Refresh token từ cookie", required = true)
            @CookieValue(value = "refresh_token") String refreshToken
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.getCurrentUserAllDeviceMetas(refreshToken)));
    }

    // ================= CHANGE PASSWORD =================
    @Operation(
            summary = "Đổi mật khẩu",
            description = "Thay đổi mật khẩu tài khoản người dùng hiện tại"
    )
    @PostMapping("/me/password")
    public ResponseEntity<ApiResponse<CurrentUserSessionResponse>> updateCurrentUserPassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.updateCurrentUserPassword(changePasswordRequest)));
    }

    // ================= CHANGE PROFILE =================
    @Operation(
            summary = "Cập nhật hồ sơ",
            description = "Cập nhật thông tin hồ sơ cá nhân"
    )
    @PostMapping("/me/profile")
    public ResponseEntity<ApiResponse<CurrentUserSessionResponse>> updateCurrentUserProfile(
            @RequestBody @Valid ChangeProfileRequest changeProfileRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.updateCurrentUserProfile(changeProfileRequest)));
    }

    // ================= CHANGE AVATAR =================
    @Operation(
            summary = "Cập nhật avatar",
            description = "Thay đổi ảnh đại diện người dùng"
    )
    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<CurrentUserSessionResponse>> updateCurrentUserAvatar(
            @Parameter(description = "File avatar", required = true)
            @RequestParam("avatar") MultipartFile avatarFile
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userAccountService.updateCurrentUserAvatar(avatarFile)));
    }

    // ================= DELETE SESSION =================
    @Operation(
            summary = "Xóa phiên đăng nhập",
            description = "Xóa một thiết bị / session đang đăng nhập"
    )
    @DeleteMapping("/me/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUserDeviceMeta(
            @Parameter(description = "Session ID", example = "abc123", required = true)
            @PathVariable String sessionId
    ) {
        userAccountService.deleteCurrentUserDeviceMeta(sessionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>());
    }
}
