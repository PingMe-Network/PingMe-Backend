package me.huynhducphu.ping_me.service.blog.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.request.blog.UpsertBlogCommentRequest;
import me.huynhducphu.ping_me.dto.response.blog.BlogCommentResponse;
import me.huynhducphu.ping_me.model.blog.BlogComment;
import me.huynhducphu.ping_me.repository.blog.BlogCommentRepository;
import me.huynhducphu.ping_me.repository.blog.BlogRepository;
import me.huynhducphu.ping_me.service.common.CurrentUserProvider;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Admin 10/24/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class BlogCommentServiceImpl implements me.huynhducphu.ping_me.service.blog.BlogCommentService {

    private final CurrentUserProvider currentUserProvider;

    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;

    private final ModelMapper modelMapper;

    @Override
    public BlogCommentResponse saveBlogComment(
            UpsertBlogCommentRequest upsertBlogCommentRequest,
            Long blogId
    ) {
        var user = currentUserProvider.get();

        var blog = blogRepository
                .findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Blog này"));

        var blogComment = new BlogComment(
                upsertBlogCommentRequest.getContent(),
                user,
                blog
        );

        blogCommentRepository.save(blogComment);

        return modelMapper.map(blogComment, BlogCommentResponse.class);
    }

    @Override
    public BlogCommentResponse updateBlogComment(
            UpsertBlogCommentRequest upsertBlogCommentRequest,
            Long blogCommentId
    ) {

        var user = currentUserProvider.get();

        var blogComment = blogCommentRepository
                .findById(blogCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận này"));

        if (!Objects.equals(blogComment.getUser().getId(), user.getId()))
            throw new AccessDeniedException("Đây không phải là bình luận của bạn");

        blogComment.setContent(upsertBlogCommentRequest.getContent());

        return modelMapper.map(
                blogCommentRepository.save(blogComment),
                BlogCommentResponse.class);
    }

    @Override
    public Page<BlogCommentResponse> getBlogCommentsByBlogId(
            Long blogId,
            Specification<BlogComment> spec,
            Pageable pageable
    ) {

        return blogCommentRepository
                .findByBlogId(blogId, spec, pageable)
                .map(blogComment ->
                        modelMapper.map(blogComment, BlogCommentResponse.class));
    }

    @Override
    public void deleteBlogComment(Long blogCommentId) {
        var user = currentUserProvider.get();

        var blogComment = blogCommentRepository
                .findById(blogCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bình luận này"));

        if (!Objects.equals(blogComment.getUser().getId(), user.getId()))
            throw new AccessDeniedException("Đây không phải là bình luận của bạn");

        blogCommentRepository.delete(blogComment);
    }


}
