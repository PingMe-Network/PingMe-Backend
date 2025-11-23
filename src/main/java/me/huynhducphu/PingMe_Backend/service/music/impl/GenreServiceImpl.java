package me.huynhducphu.PingMe_Backend.service.music.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.music.Genre;
import me.huynhducphu.PingMe_Backend.repository.music.GenreRepository;
import me.huynhducphu.PingMe_Backend.service.music.GenreService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 22/11/2025 - 2:05 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music.impl
 */

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public GenreDto getGenreById(Long id) {
        return mapToDto(genreRepository.findGenreById(id));
    }

    @Override
    public Set<GenreDto> getAllGenres() {
        Set<Genre> genres = Set.copyOf(genreRepository.findAll());
        return genres.stream().map(this::mapToDto).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public GenreDto mapToDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }
}
