package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.reels.ReelSearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelSearchHistoryRepository extends JpaRepository<ReelSearchHistory, Long> {
    List<ReelSearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    Page<ReelSearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    void deleteAllByUserId(Long userId);
}
