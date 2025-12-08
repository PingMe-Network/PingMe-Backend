package me.huynhducphu.PingMe_Backend.repository.music;

import me.huynhducphu.PingMe_Backend.model.music.Playlist;
import me.huynhducphu.PingMe_Backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(Long userId);
    Page<Playlist> findByIsPublicTrue(Pageable pageable);
}
