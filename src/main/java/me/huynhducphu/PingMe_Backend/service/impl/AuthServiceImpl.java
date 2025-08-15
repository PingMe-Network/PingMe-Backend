package me.huynhducphu.PingMe_Backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.auth.ChangePasswordRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.ChangeProfileRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.LocalLoginRequest;
import me.huynhducphu.PingMe_Backend.dto.request.auth.RegisterLocalRequest;
import me.huynhducphu.PingMe_Backend.dto.response.auth.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.auth.DefaultAuthResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserDetailResponse;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.constant.AuthProvider;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 8/3/2025
 **/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements me.huynhducphu.PingMe_Backend.service.AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${cookie.sameSite}")
    private String sameSite;

    @Value("${cookie.secure}")
    private boolean secure;

    @Override
    public UserSessionResponse registerLocal(
            RegisterLocalRequest registerLocalRequest) {
        var user = modelMapper.map(registerLocalRequest, User.class);

        if (userRepository.existsByEmail(registerLocalRequest.getEmail()))
            throw new DataIntegrityViolationException("Email đã tồn tại");

        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserSessionResponse.class);
    }

    @Override
    public AuthResultWrapper loginLocal(LocalLoginRequest localLoginRequest) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                localLoginRequest.getEmail(),
                localLoginRequest.getPassword()
        );

        var authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResultWrapper(getCurrentUser());
    }

    @Override
    public ResponseCookie logout() {
        return ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .path("/")
                .sameSite(sameSite)
                .secure(secure)
                .maxAge(0)
                .build();
    }

    @Override
    public AuthResultWrapper refreshSession(String refreshToken) {
        String email = jwtService.decodeJwt(refreshToken).getSubject();
        var user = userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        return buildAuthResultWrapper(user);
    }

    @Override
    public User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng hiện tại"));
    }

    @Override
    public UserSessionResponse getCurrentUserSession() {
        var user = getCurrentUser();
        return modelMapper.map(user, UserSessionResponse.class);
    }

    @Override
    public UserDetailResponse getCurrentUserDetail() {
        var user = getCurrentUser();
        return modelMapper.map(user, UserDetailResponse.class);
    }

    @Override
    public UserSessionResponse updateCurrentUserPassword(
            ChangePasswordRequest changePasswordRequest
    ) {
        var user = getCurrentUser();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new DataIntegrityViolationException("Mật khẩu cũ không chính xác");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        return modelMapper.map(user, UserSessionResponse.class);
    }

    @Override
    public UserSessionResponse updateCurrentUserProfile(
            ChangeProfileRequest changeProfileRequest
    ) {
        var user = getCurrentUser();
        
        user.setName(changeProfileRequest.getName());
        user.setGender(changeProfileRequest.getGender());
        user.setAddress(changeProfileRequest.getAddress());
        user.setDob(changeProfileRequest.getDob());

        return modelMapper.map(user, UserSessionResponse.class);
    }

    private AuthResultWrapper buildAuthResultWrapper(User user) {
        var accessToken = jwtService.buildJwt(user, accessTokenExpiration);
        var refreshToken = jwtService.buildJwt(user, refreshTokenExpiration);

        var defaultAuthResponseDto = new DefaultAuthResponse(
                modelMapper.map(user, UserSessionResponse.class),
                accessToken
        );
        var refreshTokenCookie = ResponseCookie
                .from("refresh_token", refreshToken)
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
