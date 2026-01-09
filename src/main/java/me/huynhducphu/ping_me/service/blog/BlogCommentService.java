package me.huynhducphu.ping_me.service.blog;

import me.huynhducphu.ping_me.dto.request.blog.UpsertBlogCommentRequest;
import me.huynhducphu.ping_me.dto.response.blog.BlogCommentResponse;
import me.huynhducphu.ping_me.model.blog.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Admin 10/24/2025
 *
 **/
public interface BlogCommentService {
    BlogCommentResponse saveBlogComment(
            UpsertBlogCommentRequest upsertBlogCommentRequest,
            Long blogId
    );

    BlogCommentResponse updateBlogComment(
            UpsertBlogCommentRequest upsertBlogCommentRequest,
            Long blogCommentId
    );

    Page<BlogCommentResponse> getBlogCommentsByBlogId(
            Long blogId,
            Specification<BlogComment> spec,
            Pageable pageable
    );

    void deleteBlogComment(Long blogCommentId);
}
