package me.huynhducphu.ping_me.dto.response.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.ping_me.dto.response.user.UserSummaryResponse;

import java.time.LocalDateTime;

/**
 * Admin 10/24/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogCommentResponse {

    private Long id;
    private String content;
    private UserSummaryResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
