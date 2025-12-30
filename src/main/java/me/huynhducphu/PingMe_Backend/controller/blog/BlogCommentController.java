package me.huynhducphu.PingMe_Backend.controller.blog;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.blog.UpsertBlogCommentRequest;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.base.PageResponse;
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
 *
 **/
@Tag(
        name = "Blog Comments",
        description = "Các endpoints liên quan đến bình luận blog"
)
@RestController
@RequestMapping("/blog-comments")
@RequiredArgsConstructor
public class BlogCommentController {

    private final BlogCommentService blogCommentService;

    @PostMapping("/blogs/{blogId}")
    public ResponseEntity<ApiResponse<BlogCommentResponse>> saveBlogComment(
            @Valid @RequestBody UpsertBlogCommentRequest upsertBlogCommentRequest,
            @PathVariable Long blogId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        blogCommentService.saveBlogComment(upsertBlogCommentRequest, blogId)
                ));
    }

    @PutMapping("/{blogCommentId}")
    public ResponseEntity<ApiResponse<BlogCommentResponse>> updateBlogComment(
            @Valid @RequestBody UpsertBlogCommentRequest upsertBlogCommentRequest,
            @PathVariable Long blogCommentId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        blogCommentService.updateBlogComment(upsertBlogCommentRequest, blogCommentId)
                ));
    }

    @GetMapping("/blogs/{blogId}")
    public ResponseEntity<ApiResponse<PageResponse<BlogCommentResponse>>> getBlogCommentsByBlogId(
            @PathVariable Long blogId,
            @PageableDefault Pageable pageable,
            @Filter Specification<BlogComment> spec
    ) {
        var page = blogCommentService.getBlogCommentsByBlogId(blogId, spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @DeleteMapping("/{blogCommentId}")
    public ResponseEntity<ApiResponse<Void>> deleteBlogComment(
            @PathVariable Long blogCommentId
    ) {
        blogCommentService.deleteBlogComment(blogCommentId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }


}
