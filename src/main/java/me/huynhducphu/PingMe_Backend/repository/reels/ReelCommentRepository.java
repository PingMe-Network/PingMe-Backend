package me.huynhducphu.PingMe_Backend.repository.reels;

import me.huynhducphu.PingMe_Backend.model.reels.ReelComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReelCommentRepository extends JpaRepository<ReelComment, Long> {
    Page<ReelComment> findByReelIdOrderByCreatedAtDesc(Long reelId, Pageable pageable);
    long countByReelId(Long reelId);
    Page<ReelComment> findByParentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable);
    Optional<ReelComment> findFirstByReelIdAndIsPinnedTrue(Long reelId);
}
