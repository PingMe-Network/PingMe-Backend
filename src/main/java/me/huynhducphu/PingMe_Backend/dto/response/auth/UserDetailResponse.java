package me.huynhducphu.PingMe_Backend.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.Gender;

import java.time.LocalDate;

/**
 * Admin 8/13/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDetailResponse {

    private String email;
    private String name;
    private String avatarUrl;
    private Gender gender;
    private String address;
    private LocalDate dob;

}
