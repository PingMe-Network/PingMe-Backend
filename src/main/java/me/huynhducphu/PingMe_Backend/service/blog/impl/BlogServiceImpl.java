package me.huynhducphu.PingMe_Backend.service.blog.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.blog.UpsertBlogRequest;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogDetailsResponse;
import me.huynhducphu.PingMe_Backend.dto.response.blog.BlogReviewResponse;
import me.huynhducphu.PingMe_Backend.model.blog.Blog;
import me.huynhducphu.PingMe_Backend.repository.BlogRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.integration.S3Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 10/13/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImpl implements me.huynhducphu.PingMe_Backend.service.blog.BlogService {
    private static final long MAX_BLOG_IMAGE_SIZE = 2 * 1024 * 1024L;

    private final CurrentUserProvider currentUserProvider;

    private final BlogRepository blogRepository;

    private final S3Service s3Service;

    private final ModelMapper modelMapper;

    /**
     * Lưu Blog bao gồm cả bài blog và ảnh
     * <p>
     * Nếu blogImg khác null sẽ gọi service
     * s3 lưu trên aws
     *
     * @return BlogReviewResponse
     */
    @Override
    public BlogReviewResponse saveBlog(
            UpsertBlogRequest dto,
            MultipartFile blogImg
    ) {
        var currentUser = currentUserProvider.get();

        var blog = new Blog(
                dto.getTitle(), dto.getDescription(),
                dto.getContent(), dto.getCategory()
        );
        blog.setUser(currentUser);

        var savedBlog = blogRepository.saveAndFlush(blog);

        if (blogImg != null && !blogImg.isEmpty()) {
            String url = s3Service.uploadFile(
                    blogImg,
                    "blog-images",
                    savedBlog.getId().toString(),
                    true,
                    MAX_BLOG_IMAGE_SIZE
            );

            blog.setImgPreviewUrl(url);
        }

        return modelMapper.map(blog, BlogReviewResponse.class);
    }

    /**
     * Cập nhật Blog bao gồm cả bài blog và ảnh
     * <p>
     * Nếu blogImg khác null sẽ gọi service
     * s3 lưu trên aws
     *
     * @return BlogReviewResponse
     */
    @Override
    public BlogReviewResponse updateBlog(
            UpsertBlogRequest dto,
            MultipartFile blogImg,
            Long blogId
    ) {
        var user = currentUserProvider.get();

        var blog = blogRepository
                .findById(blogId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Blog này"));

        if (!blog.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("Bạn không có quyền truy cập");

        blog.setTitle(dto.getTitle());
        blog.setDescription(dto.getDescription());
        blog.setContent(dto.getContent());
        blog.setCategory(dto.getCategory());

        if (blogImg != null && !blogImg.isEmpty()) {
            String url = s3Service.uploadFile(
                    blogImg,
                    "blog-images",
                    blog.getId().toString(),
                    true,
                    MAX_BLOG_IMAGE_SIZE
            );

            blog.setImgPreviewUrl(url);
        }

        return modelMapper.map(blog, BlogReviewResponse.class);
    }

    @Override
    public Page<BlogReviewResponse> getAllApprovedBlogs(
            Specification<Blog> spec,
            Pageable pageable
    ) {
        return blogRepository
                .findApprovedBlogs(spec, pageable)
                .map(blog -> modelMapper.map(blog, BlogReviewResponse.class));
    }

    @Override
    public Page<BlogReviewResponse> getAllBlogs(
            Specification<Blog> spec,
            Pageable pageable
    ) {
        return blogRepository
                .findAll(spec, pageable)
                .map(blog -> modelMapper.map(blog, BlogReviewResponse.class));
    }

    @Override
    public BlogDetailsResponse getBlogDetailsById(Long id) {
        var blog = blogRepository.findById(id).orElse(null);

        if (blog == null)
            throw new EntityNotFoundException("Không tìm thấy Blog");


        return modelMapper.map(blog, BlogDetailsResponse.class);
    }

    @Override
    public Page<BlogReviewResponse> getCurrentUserBlogs(
            Specification<Blog> spec,
            Pageable pageable
    ) {
        var user = currentUserProvider.get();

        return blogRepository
                .findByUserId(user.getId(), spec, pageable)
                .map(blog -> modelMapper.map(blog, BlogReviewResponse.class));
    }


    @Override
    public void approveBlog(Long id) {
        var blog = blogRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Blog"));

        if (blog.getIsApproved() == true)
            throw new IllegalArgumentException("Bài blog này đã được duyệt rồi");
        blog.setIsApproved(true);
    }

    @Override
    public void deleteBlog(Long id) {
        var blog = blogRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Blog"));

        blogRepository.delete(blog);
    }

}
