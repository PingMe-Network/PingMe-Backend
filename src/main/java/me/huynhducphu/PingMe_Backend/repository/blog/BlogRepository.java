package me.huynhducphu.PingMe_Backend.repository.blog;

import me.huynhducphu.PingMe_Backend.model.blog.Blog;
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
public interface BlogRepository extends
        JpaRepository<Blog, Long>,
        JpaSpecificationExecutor<Blog> {

    default Page<Blog> findApprovedBlogs(
            Specification<Blog> filterSpec,
            Pageable pageable
    ) {
        Specification<Blog> approvedSpec = (root, query, cb) ->
                cb.isTrue(root.get("isApproved"));

        Specification<Blog> combinedSpec =
                (filterSpec != null) ? approvedSpec.and(filterSpec) : approvedSpec;

        return findAll(combinedSpec, pageable);

    }

    default Page<Blog> findByUserId(Long userId, Specification<Blog> filterSpec, Pageable pageable) {
        Specification<Blog> userSpec = (root, query, cb) ->
                cb.equal(root.get("user").get("id"), userId);

        Specification<Blog> combinedSpec =
                (filterSpec != null) ? userSpec.and(filterSpec) : userSpec;

        return findAll(combinedSpec, pageable);
    }


}
