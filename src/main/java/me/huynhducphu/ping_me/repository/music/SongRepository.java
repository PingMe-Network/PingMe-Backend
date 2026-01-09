package me.huynhducphu.ping_me.repository.music;

import me.huynhducphu.ping_me.model.music.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 6:22 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.repository
 */

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    // Load Song cùng lúc với ArtistRoles, Artist, Genres và Albums để tránh lỗi LazyLoading hoặc N+1 query
    @Query("SELECT s FROM Song s " +
            "LEFT JOIN FETCH s.artistRoles ar " +
            "LEFT JOIN FETCH ar.artist " +
            "LEFT JOIN FETCH s.genres " +
            "LEFT JOIN FETCH s.albums " +
            "WHERE s.id = :id")
    Optional<Song> findByIdWithDetails(@Param("id") Long id);
    
    @Modifying
    @Transactional
    @Query("UPDATE Song s SET s.playCount = s.playCount + 1 WHERE s.id = :id")
    void incrementPlayCount(@Param("id") Long id, Long userId);

    // Bỏ tham số int limit, thay bằng Pageable
    @Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
    List<Song> findSongsByPlayCount(Pageable pageable);

    List<Song> findSongsByTitleContainingIgnoreCase(String title);

    @Query("SELECT s FROM Song s JOIN s.genres g WHERE g.id = :genreId")
    List<Song> findSongsByGenreId(@Param("genreId") Long genreId);

    @Query(value = "SELECT * FROM songs WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<Song> findSoftDeletedSong(Long id);

    @Query(value = "SELECT * FROM songs WHERE id = :id", nativeQuery = true)
    Optional<Song> findByIdIgnoringDeleted(@Param("id") Long id);
}
