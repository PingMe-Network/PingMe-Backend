package me.huynhducphu.PingMe_Backend.dto.response.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.model.constant.BlogCategory;

import java.time.LocalDateTime;

/**
 * Admin 10/13/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogReviewResponse {

    private Long id;
    private String title;
    private String description;
    private BlogCategory category;
    private UserSummaryResponse user;
    private String imgPreviewUrl;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
