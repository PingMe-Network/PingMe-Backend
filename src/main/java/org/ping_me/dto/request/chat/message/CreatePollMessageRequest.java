package org.ping_me.dto.request.chat.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin 4/23/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePollMessageRequest {

    @NotBlank(message = "Câu hỏi bình chọn không được để trống")
    @Length(max = 300, message = "Câu hỏi bình chọn không được vượt quá 300 ký tự")
    private String question;

    @NotEmpty(message = "Bình chọn cần ít nhất 2 lựa chọn")
    private List<@NotBlank(message = "Lựa chọn không được để trống") @Length(max = 120, message = "Lựa chọn không được vượt quá 120 ký tự") String> options;

    private Boolean allowMultiple = false;
    private LocalDateTime expiresAt;
    private String repliedMessageId;

    @NotBlank(message = "UUID không được để trống")
    private String clientMsgId;

    @NotNull(message = "Mã phòng không được để trống")
    private Long roomId;
}
