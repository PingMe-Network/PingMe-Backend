package me.huynhducphu.PingMe_Backend.service.blog;

import me.huynhducphu.PingMe_Backend.dto.request.blog.UpsertBlogRequest;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogDetailsResponse;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogReviewResponse;
import me.huynhducphu.PingMe_Backend.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 10/13/2025
 *
 **/
public interface BlogService {
    BlogReviewResponse saveBlog(
            UpsertBlogRequest dto,
            MultipartFile imgPreviewFile
    );

    BlogReviewResponse updateBlog(
            UpsertBlogRequest dto,
            MultipartFile blogImg,
            Long blogId
    );

    Page<BlogReviewResponse> getAllApprovedBlogs(
            Specification<Blog> spec,
            Pageable pageable
    );

    Page<BlogReviewResponse> getAllBlogs(
            Specification<Blog> spec,
            Pageable pageable
    );

    BlogDetailsResponse getBlogDetailsById(Long id);


    Page<BlogReviewResponse> getCurrentUserBlogs(
            Specification<Blog> spec,
            Pageable pageable
    );
}
