package me.huynhducphu.PingMe_Backend.dto.request.user_account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/16/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionMetaRequest {

    private String deviceType;

    private String browser;

    private String os;

}
