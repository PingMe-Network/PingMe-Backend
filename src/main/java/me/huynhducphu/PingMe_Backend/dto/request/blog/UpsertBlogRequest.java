package me.huynhducphu.PingMe_Backend.dto.request.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.BlogCategory;

/**
 * Admin 9/15/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpsertBlogRequest {

    @NotBlank(message = "Tiêu đề Blog không được bỏ trống")
    private String title;

    @NotBlank(message = "Mô tả Blog không được để trống")
    private String description;

    @NotBlank(message = "Nội dung Blog không được bỏ trống")
    private String content;

    @NotNull(message = "Chủ đề Blog không được bỏ trống")
    private BlogCategory category;


}
