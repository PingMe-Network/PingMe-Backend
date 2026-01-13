package me.huynhducphu.ping_me.repository.jpa.blog;

import me.huynhducphu.ping_me.model.blog.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Admin 9/15/2025
 *
 **/
@Repository
public interface BlogCommentRepository extends
        JpaRepository<BlogComment, Long>,
        JpaSpecificationExecutor<BlogComment> {

    default Page<BlogComment> findByBlogId(
            Long blogId,
            Specification<BlogComment> filterSpec,
            Pageable pageable
    ) {
        Specification<BlogComment> blogSpec =
                (root, query, cb)
                        -> cb.equal(root.get("blog").get("id"), blogId);

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt"))
        );

        Specification<BlogComment> combinedSpec =
                (filterSpec != null) ? blogSpec.and(filterSpec) : blogSpec;

        return findAll(combinedSpec, sortedPageable);
    }

}
