package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.music.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 6:22 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.repository
 */

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    // Load Song cùng lúc với ArtistRoles, Artist, Genres và Albums để tránh lỗi LazyLoading hoặc N+1 query
    @Query("SELECT s FROM Song s " +
            "LEFT JOIN FETCH s.artistRoles ar " +
            "LEFT JOIN FETCH ar.artist " +
            "LEFT JOIN FETCH s.genres " +
            "LEFT JOIN FETCH s.albums " +
            "WHERE s.id = :id")
    Optional<Song> findByIdWithDetails(@Param("id") Long id);

    List<Song> findSongsByTitleContainingIgnoreCase(String title);
}
