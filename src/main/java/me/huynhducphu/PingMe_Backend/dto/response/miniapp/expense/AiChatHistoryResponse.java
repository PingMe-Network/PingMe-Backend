package me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.AiChatRole;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiChatHistoryResponse {
    private Long id;
    private AiChatRole role;
    private String content;
    private LocalDateTime createdAt;
}
