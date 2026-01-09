package me.huynhducphu.ping_me.dto.response.blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.ping_me.dto.response.user.UserSummaryResponse;
import me.huynhducphu.ping_me.model.constant.BlogCategory;

import java.time.LocalDateTime;

/**
 * Admin 10/14/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogDetailsResponse {

    private Long id;
    private String title;
    private String description;
    private BlogCategory category;
    private UserSummaryResponse user;
    private String imgPreviewUrl;
    private Boolean isApproved;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
