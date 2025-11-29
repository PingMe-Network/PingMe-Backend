package me.huynhducphu.PingMe_Backend.repository.music;

import me.huynhducphu.PingMe_Backend.model.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
*@created 20/11/2025 - 6:21 PM
*@project DHKTPM18ATT_Nhom10_PingMe_Backend
*@package me.huynhducphu.PingMe_Backend.repository
*@author Le Tran Gia Huy
*/

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    @Query(value = "SELECT * FROM albums WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<Album> findSoftDeletedAlbum(@Param("id") Long id);

    @Query(value = "SELECT * FROM albums WHERE id = :id", nativeQuery = true)
    Optional<Album> findByIdIgnoringDeleted(@Param("id") Long id);
    
    List<Album> findAlbumsByTitleContainingIgnoreCase(String title);
}
