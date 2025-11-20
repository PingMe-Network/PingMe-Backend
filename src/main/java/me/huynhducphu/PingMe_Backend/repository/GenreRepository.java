package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.music.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 6:22 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.repository
 */

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
