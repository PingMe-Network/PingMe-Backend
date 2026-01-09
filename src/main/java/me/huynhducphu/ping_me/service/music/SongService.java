package me.huynhducphu.ping_me.service.music;

import me.huynhducphu.ping_me.dto.request.music.SongRequest;
import me.huynhducphu.ping_me.dto.response.music.SongResponse;

import me.huynhducphu.ping_me.dto.response.music.SongResponseWithAllAlbum;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.List;

public interface SongService {
    long MAX_AUDIO_SIZE = 20 * 1024 * 1024;
    long MAX_COVER_SIZE = 5 * 1024 * 1024;

    List<SongResponseWithAllAlbum> getAllSongs();

    SongResponse getSongById(Long id);

    List<SongResponse> getSongByTitle(String title);

    List<SongResponseWithAllAlbum> getSongByAlbum(Long id);

    List<SongResponseWithAllAlbum> getSongsByArtist(Long artistId);

    List<SongResponseWithAllAlbum> getTopPlayedSongs(int limit);

    List<SongResponseWithAllAlbum> getSongByGenre(Long id);

    List<SongResponse> save(
            SongRequest dto,
            MultipartFile musicFile,
            MultipartFile imgFile
    );

    // Trả về List vì 1 bài hát có thể thuộc nhiều album -> flatten ra nhiều dòng
    List<SongResponse> update(
            Long id,
            SongRequest dto,
            MultipartFile musicFile,
            MultipartFile imgFile
    ) throws IOException;

    void hardDelete(Long id);

    void softDelete(Long id);

    void restore(Long id);

    @Transactional
    void increasePlayCount(Long songId);
}
