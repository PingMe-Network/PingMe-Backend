package me.huynhducphu.ping_me.dto.admin.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.huynhducphu.ping_me.model.constant.AccountStatus;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DefaultUserResponse {
    Long id;
    String email;
    String name;
    AccountStatus accountStatus;
}
