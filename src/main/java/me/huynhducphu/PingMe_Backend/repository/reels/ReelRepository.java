package me.huynhducphu.PingMe_Backend.repository.reels;

import me.huynhducphu.PingMe_Backend.model.reels.Reel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReelRepository extends JpaRepository<Reel, Long> {
    Page<Reel> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
