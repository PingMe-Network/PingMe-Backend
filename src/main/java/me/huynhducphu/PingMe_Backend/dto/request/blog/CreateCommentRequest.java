package me.huynhducphu.PingMe_Backend.dto.request.blog;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 9/15/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCommentRequest {

    @NotBlank(message = "Nội dung Blog không được bỏ trống")
    private String content;

}
