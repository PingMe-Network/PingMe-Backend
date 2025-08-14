package me.huynhducphu.PingMe_Backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.user.CreateUserRequest;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.user.DefaultUserResponse;
import me.huynhducphu.PingMe_Backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Admin 8/3/2025
 **/
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<DefaultUserResponse>> saveUser(
            @RequestBody @Valid CreateUserRequest createUserRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(userService.saveUser(createUserRequest)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<DefaultUserResponse>> getAllUsers(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<DefaultUserResponse> defaultUserResponseDtoPage =
                userService.getAllUsers(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new PageResponse<>(defaultUserResponseDtoPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DefaultUserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userService.getUserById(id)));
    }


}
