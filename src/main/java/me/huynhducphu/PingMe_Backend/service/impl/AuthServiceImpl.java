package me.huynhducphu.PingMe_Backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.auth.UserLoginLocalRequestDto;
import me.huynhducphu.PingMe_Backend.dto.request.auth.UserRegisterLocalRequestDto;
import me.huynhducphu.PingMe_Backend.dto.response.auth.AuthResultWrapper;
import me.huynhducphu.PingMe_Backend.dto.response.auth.DefaultAuthResponseDto;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserSessionResponseDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 8/3/2025
 **/
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
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

    @Override
    public UserSessionResponseDto registerLocal(
            UserRegisterLocalRequestDto userRegisterLocalRequestDto) {
        var user = modelMapper.map(userRegisterLocalRequestDto, User.class);

        if (userRepository.existsByEmail(userRegisterLocalRequestDto.getEmail()))
            throw new DataIntegrityViolationException("Email đã tồn tại");

        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserSessionResponseDto.class);
    }

    @Override
    public AuthResultWrapper loginLocal(UserLoginLocalRequestDto userLoginLocalRequestDto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginLocalRequestDto.getEmail(),
                userLoginLocalRequestDto.getPassword()
        );

        var authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return buildAuthResultWrapper(getCurrentUser());
    }

    @Override
    public ResponseCookie logout() {
        return ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
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
    public UserSessionResponseDto getCurrentUserSession() {
        var user = getCurrentUser();
        return modelMapper.map(user, UserSessionResponseDto.class);
    }

    private AuthResultWrapper buildAuthResultWrapper(User user) {
        var accessToken = jwtService.buildJwt(user, accessTokenExpiration);
        var refreshToken = jwtService.buildJwt(user, refreshTokenExpiration);

        var defaultAuthResponseDto = new DefaultAuthResponseDto(
                modelMapper.map(user, UserSessionResponseDto.class),
                accessToken
        );
        var refreshTokenCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshTokenExpiration)
                .build();

        return new AuthResultWrapper(
                defaultAuthResponseDto,
                refreshTokenCookie
        );
    }

}
