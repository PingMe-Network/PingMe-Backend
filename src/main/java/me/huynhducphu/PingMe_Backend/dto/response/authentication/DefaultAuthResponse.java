package me.huynhducphu.PingMe_Backend.dto.response.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/4/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultAuthResponse {

    private CurrentUserSessionResponse userSession;
    private String accessToken;

}
