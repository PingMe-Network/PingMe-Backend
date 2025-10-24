package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    default Page<BlogComment> findByBlogId(Long blogId, Specification<BlogComment> filterSpec, Pageable pageable) {
        Specification<BlogComment> blogSpec = (root, query, cb) ->
                cb.equal(root.get("blog").get("id"), blogId);

        Specification<BlogComment> combinedSpec =
                (filterSpec != null) ? blogSpec.and(filterSpec) : blogSpec;

        return findAll(combinedSpec, pageable);
    }

}
