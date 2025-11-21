package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;

import java.util.List;

public interface SongService {
    SongResponse getSongById(Long id);
    List<SongResponse> getSongByTitle(String title);
}
