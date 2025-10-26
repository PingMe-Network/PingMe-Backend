package me.huynhducphu.PingMe_Backend.dto.response.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/17/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentUserDeviceMetaResponse {

    private String sessionId;
    private String deviceType;
    private String browser;
    private String os;
    private String lastActiveAt;
    private boolean isCurrent;

}
