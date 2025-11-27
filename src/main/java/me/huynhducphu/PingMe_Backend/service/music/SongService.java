package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.request.music.SongRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;

import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponseWithAllAlbum;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface SongService {
    long MAX_AUDIO_SIZE = 20 * 1024 * 1024;
    long MAX_COVER_SIZE = 5 * 1024 * 1024;

    List<SongResponseWithAllAlbum> getAllSongs();

    SongResponse getSongById(Long id);

    List<SongResponse> getSongByTitle(String title);

    List<SongResponse> getTopPlayedSongs(int limit);

    List<SongResponse> getSongByGenre(Long id);

    List<SongResponse> save(
            SongRequest dto,
            MultipartFile musicFile,
            MultipartFile imgFile
    );

    // Trả về List vì 1 bài hát có thể thuộc nhiều album -> flatten ra nhiều dòng
    List<SongResponse> update(Long id, SongRequest dto, MultipartFile musicFile, MultipartFile imgFile);

    void hardDelete(Long id);

    void softDelete(Long id);

    void restore(Long id);

    @Transactional
    void increasePlayCount(Long songId);
}
