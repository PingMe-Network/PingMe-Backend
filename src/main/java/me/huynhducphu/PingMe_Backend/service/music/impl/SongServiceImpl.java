package me.huynhducphu.PingMe_Backend.service.music.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.AlbumSummaryDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.ArtistSummaryDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.constant.ArtistRole;
import me.huynhducphu.PingMe_Backend.model.music.Album;
import me.huynhducphu.PingMe_Backend.model.music.Artist;
import me.huynhducphu.PingMe_Backend.model.music.Song;
import me.huynhducphu.PingMe_Backend.model.music.SongArtistRole;
import me.huynhducphu.PingMe_Backend.repository.music.AlbumRepository;
import me.huynhducphu.PingMe_Backend.repository.music.GenreRepository;
import me.huynhducphu.PingMe_Backend.repository.music.SongRepository;
import me.huynhducphu.PingMe_Backend.service.music.SongService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;

    @Override
    public SongResponse getSongById(Long id) {
        Song song = songRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài hát với id: " + id));

        return mapToSongResponse(song, song.getAlbums() != null && !song.getAlbums().isEmpty()
                ? song.getAlbums().iterator().next()
                : null);
    }

    @Override
    public List<SongResponse> getSongByTitle(String title) {
        //Lấy danh sách bài hát từ DB theo title (case-insensitive)
        List<Song> songs = songRepository.findSongsByTitleContainingIgnoreCase(title);

        return flattenSongsWithAlbums(songs);
    }

    public List<SongResponse> getSongByGenre(GenreDto genreDto) {
        List<Song> songs = songRepository.findSongsByGenresContainingIgnoreCase(
                genreRepository.findGenreByName(
                        genreDto.getName()
        ));

        return flattenSongsWithAlbums(songs);
    }

    private List<SongResponse> flattenSongsWithAlbums(List<Song> songs) {
        List<SongResponse> result = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbums() != null && !song.getAlbums().isEmpty()) {
                for (Album album : song.getAlbums()) {
                    result.add(mapToSongResponse(song, album));
                }
            } else {
                // Nếu song không có album, vẫn trả về song với album = null
                result.add(mapToSongResponse(song, null));
            }
        }
        return result;
    }

    private SongResponse mapToSongResponse(Song song, Album album) {
        SongResponse response = new SongResponse();

        // --- Map các trường cơ bản ---
        response.setId(song.getId());
        response.setTitle(song.getTitle());
        response.setDuration(song.getDuration());
        response.setSongUrl(song.getSongUrl());
        response.setCoverImageUrl(song.getImgUrl());

        // --- Xử lý Artist ---
        List<SongArtistRole> roles = song.getArtistRoles();

        // Main Artist
        Optional<Artist> mainArtistOpt = roles.stream()
                .filter(r -> r.getRole() == ArtistRole.MAIN_ARTIST)
                .map(SongArtistRole::getArtist)
                .findFirst();
        mainArtistOpt.ifPresent(a -> response.setMainArtist(
                new ArtistSummaryDto(a.getId(), a.getName(), a.getImgUrl())
        ));

        // Featured Artists
        List<ArtistSummaryDto> featuredList = roles.stream()
                .filter(r -> r.getRole() == ArtistRole.FEATURED_ARTIST)
                .map(r -> {
                    Artist a = r.getArtist();
                    return new ArtistSummaryDto(a.getId(), a.getName(), a.getImgUrl());
                })
                .collect(Collectors.toList());
        response.setFeaturedArtists(featuredList);

        // --- Xử lý Genres ---
        List<GenreDto> genreDtos = song.getGenres().stream()
                .map(g -> new GenreDto(g.getId(), g.getName()))
                .collect(Collectors.toList());
        response.setGenres(genreDtos);

        // --- Xử lý Album ---
        if (album != null) {
            response.setAlbum(new AlbumSummaryDto(album.getId(), album.getTitle()));
        }

        return response;
    }
}
