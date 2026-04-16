package org.ping_me.dto.request.chat.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Admin 4/16/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditMessageRequest {

    @NotBlank(message = "Nội dung không được để trống")
    @Length(max = 1000, message = "Nội dung không được vượt quá 1000 ký tự")
    private String content;
}
