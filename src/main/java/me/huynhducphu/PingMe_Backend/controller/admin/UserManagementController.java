package me.huynhducphu.PingMe_Backend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.admin.request.user.CreateUserRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.admin.response.user.DefaultUserResponse;
import me.huynhducphu.PingMe_Backend.service.admin.UserManagementService;
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
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping
    public ResponseEntity<ApiResponse<DefaultUserResponse>> saveUser(
            @RequestBody @Valid CreateUserRequest createUserRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(userManagementService.saveUser(createUserRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DefaultUserResponse>>> getAllUsers(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<DefaultUserResponse> defaultUserResponseDtoPage =
                userManagementService.getAllUsers(pageable);

        PageResponse<DefaultUserResponse> pageResponse =
                new PageResponse<>(defaultUserResponseDtoPage);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(pageResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DefaultUserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userManagementService.getUserById(id)));
    }


}
