package me.huynhducphu.PingMe_Backend.dto.response.chat.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.RoomRole;

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
    private RoomRole role;
    private Long lastReadMessageId;
    private LocalDateTime lastReadAt;
}
