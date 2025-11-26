package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.request.music.GenreRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.GenreResponse;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.music.Genre;

import java.util.List;
import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 22/11/2025 - 9:13 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music
 */
public interface GenreService {
    GenreResponse getGenreById(Long id);

    List<GenreResponse> getAllGenres();

    GenreResponse createGenre(GenreRequest request);

    GenreResponse updateGenre(Long id, GenreRequest request);

    void softDeleteGenre(Long id);

    void restoreGenre(Long id);

    void hardDeleteGenre(Long id);
}
