package me.huynhducphu.PingMe_Backend.dto.request.chat.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.MessageType;

/**
 * Admin 8/26/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMessageRequest {
    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @NotBlank(message = "UUID không được để trống")
    private String clientMsgId;

    @Schema(allowableValues = {"TEXT", "IMAGE", "VIDEO", "FILE"})
    private MessageType type;

    @NotNull(message = "Mã phòng không được để trống")
    private Long roomId;
}
