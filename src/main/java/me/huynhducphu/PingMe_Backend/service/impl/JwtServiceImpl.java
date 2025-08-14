package me.huynhducphu.PingMe_Backend.service.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.config.auth.AuthConfiguration;
import me.huynhducphu.PingMe_Backend.dto.response.auth.UserSessionResponse;
import me.huynhducphu.PingMe_Backend.model.User;
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
public class JwtServiceImpl implements me.huynhducphu.PingMe_Backend.service.JwtService {

    private final ModelMapper modelMapper;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    @Override
    public String buildJwt(User user, Long expirationRate) {

        Instant now = Instant.now();
        Instant validity = now.plus(expirationRate, ChronoUnit.SECONDS);

        JwsHeader jwsHeader = JwsHeader.with(AuthConfiguration.MAC_ALGORITHM).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getEmail())
                .claim("user", modelMapper.map(user, UserSessionResponse.class))
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }

    @Override
    public Jwt decodeJwt(String token) {
        return jwtDecoder.decode(token);
    }

}
