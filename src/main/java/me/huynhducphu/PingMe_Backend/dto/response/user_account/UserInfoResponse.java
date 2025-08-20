package me.huynhducphu.PingMe_Backend.dto.response.user_account;

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
public class UserInfoResponse {

    private String email;
    private String name;
    private String avatarUrl;
    private Gender gender;
    private String address;
    private LocalDate dob;

}
