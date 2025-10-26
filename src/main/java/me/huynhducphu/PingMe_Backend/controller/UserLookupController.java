package me.huynhducphu.PingMe_Backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.service.user_lookup.UserLookupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/19/2025
 **/
@Tag(
        name = "User Lookup",
        description = "Các endpoints tra cứu người dùng"
)
@RestController
@RequestMapping("/users/lookup")
@RequiredArgsConstructor
public class UserLookupController {

    private final UserLookupService userLookupService;

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> lookupUser(
            @PathVariable String email
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(userLookupService.lookupUser(email)));
    }

}
