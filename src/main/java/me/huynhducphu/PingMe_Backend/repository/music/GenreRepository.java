package me.huynhducphu.PingMe_Backend.repository.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.music.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 6:22 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.repository
 */

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Set<Genre> findGenreByName(String name);

    Genre findGenreById(Long id);
}
