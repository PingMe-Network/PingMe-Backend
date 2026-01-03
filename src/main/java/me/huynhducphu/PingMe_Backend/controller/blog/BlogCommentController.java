package me.huynhducphu.PingMe_Backend.controller.blog;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.base.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.request.blog.UpsertBlogCommentRequest;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogCommentResponse;
import me.huynhducphu.PingMe_Backend.model.blog.BlogComment;
import me.huynhducphu.PingMe_Backend.service.blog.BlogCommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 10/24/2025
 */
@Tag(
        name = "Blog Comments",
        description = "Các endpoints liên quan đến bình luận blog"
)
@RestController
@RequestMapping("/blog-comments")
@RequiredArgsConstructor
public class BlogCommentController {

    private final BlogCommentService blogCommentService;

    // ================= CREATE =================
    @Operation(
            summary = "Tạo bình luận blog",
            description = "Thêm mới một bình luận cho blog theo blogId"
    )
    @PostMapping("/blogs/{blogId}")
    public ResponseEntity<ApiResponse<BlogCommentResponse>> saveBlogComment(
            @Parameter(description = "Nội dung bình luận", required = true)
            @Valid @RequestBody UpsertBlogCommentRequest upsertBlogCommentRequest,

            @Parameter(description = "ID blog", example = "1", required = true)
            @PathVariable Long blogId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        blogCommentService.saveBlogComment(upsertBlogCommentRequest, blogId)
                ));
    }

    // ================= UPDATE =================
    @Operation(
            summary = "Cập nhật bình luận",
            description = "Chỉnh sửa nội dung bình luận blog"
    )
    @PutMapping("/{blogCommentId}")
    public ResponseEntity<ApiResponse<BlogCommentResponse>> updateBlogComment(
            @Parameter(description = "Nội dung bình luận cập nhật", required = true)
            @Valid @RequestBody UpsertBlogCommentRequest upsertBlogCommentRequest,

            @Parameter(description = "ID bình luận", example = "10", required = true)
            @PathVariable Long blogCommentId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        blogCommentService.updateBlogComment(upsertBlogCommentRequest, blogCommentId)
                ));
    }

    // ================= LIST =================
    @Operation(
            summary = "Danh sách bình luận theo blog",
            description = """
                    Lấy danh sách bình luận của một blog.
                    Hỗ trợ:
                    - Phân trang (page, size, sort)
                    - Filter động bằng Spring Filter
                    """
    )
    @GetMapping("/blogs/{blogId}")
    public ResponseEntity<ApiResponse<PageResponse<BlogCommentResponse>>> getBlogCommentsByBlogId(
            @Parameter(description = "ID blog", example = "1", required = true)
            @PathVariable Long blogId,

            @Parameter(description = "Thông tin phân trang")
            @PageableDefault Pageable pageable,

            @Parameter(description = "Điều kiện filter (Spring Filter)")
            @Filter Specification<BlogComment> spec
    ) {
        var page = blogCommentService.getBlogCommentsByBlogId(blogId, spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    // ================= DELETE =================
    @Operation(
            summary = "Xóa bình luận",
            description = "Xóa một bình luận blog theo ID"
    )
    @DeleteMapping("/{blogCommentId}")
    public ResponseEntity<ApiResponse<Void>> deleteBlogComment(
            @Parameter(description = "ID bình luận", example = "10", required = true)
            @PathVariable Long blogCommentId
    ) {
        blogCommentService.deleteBlogComment(blogCommentId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }
}
