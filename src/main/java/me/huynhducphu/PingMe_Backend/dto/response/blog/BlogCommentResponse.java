package me.huynhducphu.PingMe_Backend.dto.response.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;

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
