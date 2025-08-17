package me.huynhducphu.PingMe_Backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.auth.DefaultAuthResponse;
import org.springframework.http.ResponseCookie;

/**
 * Admin 8/4/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResultWrapper {

    private DefaultAuthResponse defaultAuthResponse;
    private ResponseCookie refreshTokenCookie;

}
