package me.huynhducphu.ping_me.service.blog;

import me.huynhducphu.ping_me.dto.request.blog.UpsertBlogRequest;
import me.huynhducphu.ping_me.dto.response.blog.BlogDetailsResponse;
import me.huynhducphu.ping_me.dto.response.blog.BlogReviewResponse;
import me.huynhducphu.ping_me.model.blog.Blog;
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

    void approveBlog(Long id);

    void deleteBlog(Long id);
}
