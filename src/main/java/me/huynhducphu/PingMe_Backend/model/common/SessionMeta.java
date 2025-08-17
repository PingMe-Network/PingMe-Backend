package me.huynhducphu.PingMe_Backend.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Admin 8/16/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionMeta {

    private String sessionId;
    private String deviceType;
    private String browser;
    private String os;
    private String lastActiveAt;

}
