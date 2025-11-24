package me.huynhducphu.PingMe_Backend.service.music.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.AlbumSummaryDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.ArtistSummaryDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.model.constant.ArtistRole;
import me.huynhducphu.PingMe_Backend.model.music.*;
import me.huynhducphu.PingMe_Backend.repository.music.GenreRepository;
import me.huynhducphu.PingMe_Backend.repository.music.SongPlayHistoryRepository;
import me.huynhducphu.PingMe_Backend.repository.music.SongRepository;
import me.huynhducphu.PingMe_Backend.service.music.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final SongPlayHistoryRepository songPlayHistoryRepository;

    @Autowired
    @Qualifier("redisMessageStringTemplate")
    private RedisTemplate<String, String> redis;


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

    // Cho phép truyền số lượng bài muốn lấy
    public List<SongResponse> getTopPlayedSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Song> topSongs = songRepository.findSongsByPlayCount(pageable);
        return flattenSongsWithAlbums(topSongs);
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

    @Transactional
    @Override
    public void increasePlayCount(Long songId, Long userId) {
        String redisKey = "play:" + userId + ":" + songId;

        // Nếu trong 10s đã nghe → không tăng tiếp
        Boolean alreadyPlayed = redis.hasKey(redisKey);
        if (Boolean.TRUE.equals(alreadyPlayed)) return;

        // Tăng playCount
        songRepository.incrementPlayCount(songId);

        // Lấy song để log lịch sử
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));

        // Lưu lịch sử nghe
        songPlayHistoryRepository.save(
                SongPlayHistory.builder()
                        .song(song)
                        .userId(userId)
                        .playedAt(LocalDateTime.now())
                        .build()
        );

        // Set key Redis sống 10s → debounce
        redis.opsForValue().set(redisKey, "1", Duration.ofSeconds(10));
    }

    private SongResponse mapToSongResponse(Song song, Album album) {
        SongResponse response = new SongResponse();

        // --- Map các trường cơ bản ---
        response.setId(song.getId());
        response.setTitle(song.getTitle());
        response.setDuration(song.getDuration());
        response.setPlayCount(song.getPlayCount());
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
            response.setAlbum(new AlbumSummaryDto(album.getId(), album.getTitle(), album.getPlayCount()));
        }

        return response;
    }
}
