package me.huynhducphu.ping_me.dto.response.chat.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.ping_me.model.constant.RoomRole;
import me.huynhducphu.ping_me.model.constant.UserStatus;

import java.time.LocalDateTime;

/**
 * Admin 8/25/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomParticipantResponse {
    private Long userId;
    private String name;
    private String avatarUrl;
    private UserStatus status;
    private RoomRole role;
    private String lastReadMessageId;
    private LocalDateTime lastReadAt;
}
