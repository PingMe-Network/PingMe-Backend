package me.huynhducphu.PingMe_Backend.controller.blog;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.blog.UpsertBlogRequest;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.base.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogDetailsResponse;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogReviewResponse;
import me.huynhducphu.PingMe_Backend.model.blog.Blog;
import me.huynhducphu.PingMe_Backend.service.blog.BlogService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 10/13/2025
 *
 **/
@Tag(
        name = "Blogs",
        description = "Các endpoints liên quan đến Blogs"
)
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<ApiResponse<BlogReviewResponse>> saveBlog(
            @Valid @RequestPart("blog") UpsertBlogRequest upsertBlogRequest,
            @RequestPart(value = "blogImage", required = false) MultipartFile blogImg
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(blogService.saveBlog(upsertBlogRequest, blogImg)));
    }

    @PutMapping("/{blogId}")
    public ResponseEntity<ApiResponse<BlogReviewResponse>> updateBlog(
            @PathVariable Long blogId,
            @Valid @RequestPart("blog") UpsertBlogRequest upsertBlogRequest,
            @RequestPart(value = "blogImage", required = false) MultipartFile blogImg
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(blogService.updateBlog(upsertBlogRequest, blogImg, blogId)));
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<PageResponse<BlogReviewResponse>>> getAllApprovedBlogs(
            @Filter Specification<Blog> spec,
            @PageableDefault Pageable pageable
    ) {
        var page = blogService.getAllApprovedBlogs(spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(res));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BlogReviewResponse>>> getAllBlogs(
            @Filter Specification<Blog> spec,
            @PageableDefault() Pageable pageable
    ) {
        var page = blogService.getAllBlogs(spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(res));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<BlogReviewResponse>>> getCurrentUserBlogs(
            @Filter Specification<Blog> spec,
            @PageableDefault Pageable pageable
    ) {
        var page = blogService.getCurrentUserBlogs(spec, pageable);
        var res = new PageResponse<>(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(res));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<BlogDetailsResponse>> getBlogDetailsById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(blogService.getBlogDetailsById(id)));
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<Void>> approveBlog(@PathVariable Long id) {
        blogService.approveBlog(id);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }


}
