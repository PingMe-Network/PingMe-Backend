package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.music.Genre;

import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 22/11/2025 - 9:13 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music
 */
public interface GenreService {
    GenreDto getGenreById(Long id);

    Set<GenreDto> getAllGenres();

    GenreDto mapToDto(Genre genre);
}
