package me.huynhducphu.PingMe_Backend.dto.request.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UpsertBlogCommentRequest {

    @NotBlank(message = "Nội dung bình luận không được bỏ trống")
    @Size(max = 500, message = "Mô tả bình luận không quá 500 ký tự")
    private String content;

}
