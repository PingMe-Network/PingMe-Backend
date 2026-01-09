package me.huynhducphu.ping_me.controller.blog;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.base.ApiResponse;
import me.huynhducphu.ping_me.dto.base.PageResponse;
import me.huynhducphu.ping_me.dto.request.blog.UpsertBlogRequest;
import me.huynhducphu.ping_me.dto.response.blog.BlogDetailsResponse;
import me.huynhducphu.ping_me.dto.response.blog.BlogReviewResponse;
import me.huynhducphu.ping_me.model.blog.Blog;
import me.huynhducphu.ping_me.service.blog.BlogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 10/13/2025
 */
@Tag(
        name = "Blogs",
        description = "Các endpoints liên quan đến Blogs"
)
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @Operation(
            summary = "Tạo blog mới",
            description = "Tạo blog mới (multipart/form-data, có thể kèm hình ảnh)"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<BlogReviewResponse>> saveBlog(
            @Parameter(description = "Thông tin blog")
            @Valid @RequestPart("blog") UpsertBlogRequest upsertBlogRequest,

            @Parameter(description = "Ảnh blog (không bắt buộc)")
            @RequestPart(value = "blogImage", required = false) MultipartFile blogImg
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(blogService.saveBlog(upsertBlogRequest, blogImg)));
    }

    @Operation(
            summary = "Cập nhật blog",
            description = "Cập nhật nội dung blog theo ID"
    )
    @PutMapping("/{blogId}")
    public ResponseEntity<ApiResponse<BlogReviewResponse>> updateBlog(
            @Parameter(description = "ID của blog")
            @PathVariable Long blogId,

            @Parameter(description = "Thông tin blog")
            @Valid @RequestPart("blog") UpsertBlogRequest upsertBlogRequest,

            @Parameter(description = "Ảnh blog (không bắt buộc)")
            @RequestPart(value = "blogImage", required = false) MultipartFile blogImg
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(blogService.updateBlog(upsertBlogRequest, blogImg, blogId)));
    }

    @Operation(
            summary = "Danh sách blog đã duyệt",
            description = "Lấy danh sách blog đã được approve (hỗ trợ filter & phân trang)"
    )
    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<PageResponse<BlogReviewResponse>>> getAllApprovedBlogs(
            @Parameter(hidden = true)
            @Filter Specification<Blog> spec,

            @Parameter(hidden = true)
            @PageableDefault Pageable pageable
    ) {
        var page = blogService.getAllApprovedBlogs(spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @Operation(
            summary = "Danh sách tất cả blog",
            description = "Lấy toàn bộ blog trong hệ thống (Admin)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BlogReviewResponse>>> getAllBlogs(
            @Parameter(hidden = true)
            @Filter Specification<Blog> spec,

            @Parameter(hidden = true)
            @PageableDefault Pageable pageable
    ) {
        var page = blogService.getAllBlogs(spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @Operation(
            summary = "Danh sách blog của tôi",
            description = "Lấy blog của user đang đăng nhập"
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<BlogReviewResponse>>> getCurrentUserBlogs(
            @Parameter(hidden = true)
            @Filter Specification<Blog> spec,

            @Parameter(hidden = true)
            @PageableDefault Pageable pageable
    ) {
        var page = blogService.getCurrentUserBlogs(spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @Operation(
            summary = "Chi tiết blog",
            description = "Lấy chi tiết blog theo ID"
    )
    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<BlogDetailsResponse>> getBlogDetailsById(
            @Parameter(description = "ID của blog")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(blogService.getBlogDetailsById(id))
        );
    }

    @Operation(
            summary = "Duyệt blog",
            description = "Admin duyệt blog"
    )
    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<Void>> approveBlog(
            @Parameter(description = "ID của blog")
            @PathVariable Long id
    ) {
        blogService.approveBlog(id);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }

    @Operation(
            summary = "Xóa blog",
            description = "Xóa blog theo ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(
            @Parameter(description = "ID của blog")
            @PathVariable Long id
    ) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }
}
