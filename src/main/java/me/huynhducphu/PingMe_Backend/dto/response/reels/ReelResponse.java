package me.huynhducphu.PingMe_Backend.dto.response.reels;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReelResponse {
    private Long id;
    private String videoUrl;
    private String caption;

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLikedByMe;
    private Boolean isSavedByMe;

    private Long userId;
    private String userName;
    private String userAvatarUrl;

    private LocalDateTime createdAt;
}
