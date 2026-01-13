package me.huynhducphu.ping_me.repository.jpa.reels;

import me.huynhducphu.ping_me.model.reels.ReelView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReelViewRepository extends JpaRepository<ReelView, Long> {
    Page<ReelView> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByReelIdAndUserId(Long reelId, Long userId);
}

