package me.huynhducphu.PingMe_Backend.dto.response.user_account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSessionResponse {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private String updatedAt;

}
