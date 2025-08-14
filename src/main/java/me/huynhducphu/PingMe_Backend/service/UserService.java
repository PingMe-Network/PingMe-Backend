package me.huynhducphu.PingMe_Backend.service;

import me.huynhducphu.PingMe_Backend.dto.request.user.CreateUserRequest;
import me.huynhducphu.PingMe_Backend.dto.response.user.DefaultUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin 8/3/2025
 **/
public interface UserService {
    DefaultUserResponse saveUser(CreateUserRequest createUserRequest);

    Page<DefaultUserResponse> getAllUsers(Pageable pageable);

    DefaultUserResponse getUserById(Long id);
}
