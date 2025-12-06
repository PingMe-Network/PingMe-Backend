package me.huynhducphu.PingMe_Backend.repository.reels;

import me.huynhducphu.PingMe_Backend.model.reels.Reel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReelRepository extends JpaRepository<Reel, Long> {
    Page<Reel> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find reels created by specific user
    Page<Reel> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // search by caption (title) containing text, case-insensitive using native SQL
    @Query(value = "SELECT * FROM reels r WHERE LOWER(r.caption) LIKE LOWER(CONCAT('%', :q, '%')) ORDER BY r.created_at DESC", nativeQuery = true)
    Page<Reel> searchByTitle(@Param("q") String q, Pageable pageable);

    // search by hashtag (match anywhere in hashtags column). Pass tag including '#' (e.g. '#fun')
    @Query(value = "SELECT * FROM reels r WHERE r.hashtags IS NOT NULL AND LOWER(r.hashtags) LIKE LOWER(CONCAT('%', :tag, '%')) ORDER BY r.created_at DESC", nativeQuery = true)
    Page<Reel> searchByHashtag(@Param("tag") String tag, Pageable pageable);
}
