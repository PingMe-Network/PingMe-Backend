package me.huynhducphu.PingMe_Backend.dto.admin.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultUserResponse {

    private Long id;
    private String email;
    private String name;

}
