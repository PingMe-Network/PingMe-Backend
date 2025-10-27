package me.huynhducphu.PingMe_Backend.service.authentication.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.config.auth.AuthConfiguration;
import me.huynhducphu.PingMe_Backend.dto.response.authentication.CurrentUserSessionResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.service.authentication.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Admin 8/3/2025
 **/
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final ModelMapper modelMapper;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    @Override
    public String buildJwt(User user, Long expirationRate) {
        // Lấy thời điểm hiện tại
        Instant now = Instant.now();

        // Tính toán thời điểm JWT sẽ hết hạn
        Instant validity = now.plus(expirationRate, ChronoUnit.SECONDS);

        // Khai báo phần Header của JWT
        // Ở đây chứa thông tin về thuật toán ký (MAC algorithm) mà hệ thống đang dùng
        JwsHeader jwsHeader = JwsHeader.with(AuthConfiguration.MAC_ALGORITHM).build();

        // Khai báo phần Body (Claims) của JWT, bao gồm:
        // + issuedAt: thời điểm token được tạo ra
        // + expiresAt: thời điểm token hết hạn
        // + subject: email của người dùng (được dùng làm định danh chính)
        // + claim "user": thông tin cơ bản của người dùng, được map sang DTO UserSessionResponse
        // + claim "role": tên chức vụ của người dùng
        String roleName = user.getRole() != null ? user.getRole().getName() : "";
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getEmail())
                .claim("user", modelMapper.map(user, CurrentUserSessionResponse.class))
                .claim("role", roleName)
                .build();

        // Cuối cùng, encode JWT và lấy ra chuỗi token trả về
        return jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }


    @Override
    public Jwt decodeJwt(String token) {
        return jwtDecoder.decode(token);
    }

}
