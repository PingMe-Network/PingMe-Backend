package me.huynhducphu.PingMe_Backend.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.common.SessionMeta;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSessionResponse {

    private String email;
    private String name;
    private String avatarUrl;
    private String updatedAt;
    
}
