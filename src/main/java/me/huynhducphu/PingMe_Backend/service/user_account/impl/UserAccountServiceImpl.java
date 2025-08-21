package me.huynhducphu.PingMe_Backend.service.user_account.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.user_account.*;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.common.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.DefaultAuthResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserDeviceMetaResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserInfoResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user_account.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.constant.AuthProvider;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.user_account.UserAccountService;
import me.huynhducphu.PingMe_Backend.service.user_account.JwtService;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.integration.RefreshTokenRedisService;
import me.huynhducphu.PingMe_Backend.service.integration.S3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin 8/3/2025
 **/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final S3Service s3Service;
    private final RefreshTokenRedisService refreshTokenRedisService;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final CurrentUserProvider currentUserProvider;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final Long MAX_AVATAR_FILE_SIZE = 2 * 1024 * 1024L;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${cookie.sameSite}")
    private String sameSite;

    @Value("${cookie.secure}")
    private boolean secure;

    @Override
    public UserSessionResponse register(
            RegisterRequest registerRequest) {
        var user = modelMapper.map(registerRequest, User.class);

        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new DataIntegrityViolationException("Email đã tồn tại");

        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserSessionResponse.class);
    }

    @Override
    public AuthResultWrapper login(LoginRequest loginRequest) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        var authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResultWrapper(currentUserProvider.get(), loginRequest.getSessionMetaRequest());
    }

    @Override
    public ResponseCookie logout(String refreshToken) {
        if (refreshToken != null) {
            String email = jwtService.decodeJwt(refreshToken).getSubject();
            var refreshTokenUser = userRepository
                    .getUserByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

            refreshTokenRedisService.deleteRefreshToken(refreshToken, refreshTokenUser.getId().toString());
        }

        return ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .sameSite(sameSite)
                .secure(secure)
                .maxAge(0)
                .build();
    }

    @Override
    public AuthResultWrapper refreshSession(
            String refreshToken, SessionMetaRequest sessionMetaRequest
    ) {
        String email = jwtService.decodeJwt(refreshToken).getSubject();
        var refreshTokenUser = userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (!refreshTokenRedisService.validateToken(refreshToken, refreshTokenUser.getId().toString()))
            throw new AccessDeniedException("Không có quyền truy cập");

        refreshTokenRedisService.deleteRefreshToken(refreshToken, refreshTokenUser.getId().toString());

        return buildAuthResultWrapper(refreshTokenUser, sessionMetaRequest);
    }

    @Override
    public UserSessionResponse getCurrentUserSession() {
        var user = currentUserProvider.get();
        return modelMapper.map(user, UserSessionResponse.class);
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        var user = currentUserProvider.get();
        return modelMapper.map(user, UserInfoResponse.class);
    }

    @Override
    public List<UserDeviceMetaResponse> getCurrentUserAllDeviceMetas(
            String refreshToken
    ) {
        var currentUser = currentUserProvider.get();

        String email = jwtService.decodeJwt(refreshToken).getSubject();
        var refreshTokenUser = userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (!refreshTokenUser.getId().equals(currentUser.getId()))
            throw new AccessDeniedException("Không có quyền truy cập");

        return refreshTokenRedisService.getAllDeviceMetas(currentUser.getId().toString(), refreshToken);
    }

    @Override
    public UserSessionResponse updateCurrentUserPassword(
            ChangePasswordRequest changePasswordRequest
    ) {
        var user = currentUserProvider.get();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new DataIntegrityViolationException("Mật khẩu cũ không chính xác");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        return modelMapper.map(user, UserSessionResponse.class);
    }

    @Override
    public UserSessionResponse updateCurrentUserProfile(
            ChangeProfileRequest changeProfileRequest
    ) {
        var user = currentUserProvider.get();

        user.setName(changeProfileRequest.getName());
        user.setGender(changeProfileRequest.getGender());
        user.setAddress(changeProfileRequest.getAddress());
        user.setDob(changeProfileRequest.getDob());

        return modelMapper.map(user, UserSessionResponse.class);
    }

    @Override
    public UserSessionResponse updateCurrentUserAvatar(
            MultipartFile avatarFile
    ) {
        var user = currentUserProvider.get();

        String url = s3Service.uploadFile(
                avatarFile,
                "avatar",
                user.getEmail(),
                true,
                MAX_AVATAR_FILE_SIZE
        );

        user.setAvatarUrl(url);
        user.setUpdatedAt(LocalDateTime.now());

        return modelMapper.map(user, UserSessionResponse.class);
    }

    @Override
    public void deleteCurrentUserDeviceMeta(String sessionId) {
        String[] part = sessionId.split(":");
        String sessionUserId = part[3];

        var currentUser = currentUserProvider.get();

        if (!currentUser.getId().toString().equals(sessionUserId))
            throw new AccessDeniedException("Không có quyền truy cập");

        refreshTokenRedisService.deleteRefreshToken(sessionId);
    }

    // =====================================
    // Utilities methods
    // =====================================
    private AuthResultWrapper buildAuthResultWrapper(
            User user,
            SessionMetaRequest sessionMetaRequest
    ) {
        // ================================================
        // HANDLE ACCESS TOKEN
        // ================================================
        var accessToken = jwtService.buildJwt(user, accessTokenExpiration);
        var defaultAuthResponseDto = new DefaultAuthResponse(
                modelMapper.map(user, UserSessionResponse.class),
                accessToken
        );

        // ================================================
        // HANDLE REFRESH TOKEN
        // ================================================
        var refreshToken = jwtService.buildJwt(user, refreshTokenExpiration);
        refreshTokenRedisService.saveRefreshToken(
                refreshToken,
                user.getId().toString(),
                sessionMetaRequest,
                Duration.ofSeconds(refreshTokenExpiration)
        );

        var refreshTokenCookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .path("/")
                .sameSite(sameSite)
                .secure(secure)
                .maxAge(refreshTokenExpiration)
                .build();

        return new AuthResultWrapper(
                defaultAuthResponseDto,
                refreshTokenCookie
        );
    }

}
