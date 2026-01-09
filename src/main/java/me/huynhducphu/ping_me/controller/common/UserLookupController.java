package me.huynhducphu.ping_me.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.base.ApiResponse;
import me.huynhducphu.ping_me.dto.response.user.UserSummaryResponse;
import me.huynhducphu.ping_me.dto.response.user.UserSummarySimpleResponse;
import me.huynhducphu.ping_me.service.user.UserLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/19/2025
 **/
@Tag(
        name = "User Lookup",
        description = "Các endpoints tra cứu thông tin người dùng"
)
@RestController
@RequestMapping("/users/lookup")
@RequiredArgsConstructor
public class UserLookupController {

    private final UserLookupService userLookupService;

    @Operation(
            summary = "Tra cứu người dùng theo email",
            description = "Trả về thông tin chi tiết người dùng dựa trên email"
    )
    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> lookupUser(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(userLookupService.lookupUser(email))
        );
    }

    @Operation(
            summary = "Tra cứu người dùng theo ID",
            description = "Trả về thông tin rút gọn của người dùng theo ID"
    )
    @GetMapping("/id")
    public ResponseEntity<ApiResponse<UserSummarySimpleResponse>> lookupUserById(
            @Parameter(description = "ID người dùng", required = true)
            @RequestParam Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(userLookupService.lookupUserById(id))
        );
    }
}

