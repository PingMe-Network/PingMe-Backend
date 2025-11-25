package me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatRequest {
    @NotBlank(message = "Prompt không được để trống")
    private String prompt;
    private Integer month;
    private Integer year;
}