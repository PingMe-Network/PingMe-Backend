package me.huynhducphu.PingMe_Backend.dto.response.user_account;

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

    private UserSessionResponse userSession;
    private String accessToken;

}
