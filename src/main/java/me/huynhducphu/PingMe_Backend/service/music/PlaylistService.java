package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDetailDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDto;

import java.util.List;

public interface PlaylistService {
    PlaylistDto createPlaylist(PlaylistDto dto);

    List<PlaylistDto> getPlaylistsByUser();

    PlaylistDetailDto getPlaylistDetail(Long playlistId);

    void deletePlaylist(Long playlistId);

    void addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistId, Long songId);

    PlaylistDto updatePlaylist(Long playlistId, PlaylistDto dto);

    void reorderPlaylist(Long playlistId, List<Long> orderedSongIds);
}
