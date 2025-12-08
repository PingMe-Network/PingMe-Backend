package me.huynhducphu.PingMe_Backend.service.music.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDetailDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDto;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.music.Playlist;
import me.huynhducphu.PingMe_Backend.model.music.PlaylistSong;
import me.huynhducphu.PingMe_Backend.repository.auth.UserRepository;
import me.huynhducphu.PingMe_Backend.repository.music.PlaylistRepository;
import me.huynhducphu.PingMe_Backend.repository.music.PlaylistSongRepository;
import me.huynhducphu.PingMe_Backend.repository.music.SongRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.music.PlaylistService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;


    @Override
    public PlaylistDto createPlaylist(PlaylistDto dto) {
        var userId = currentUserProvider.get().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist p = new Playlist();
        p.setUser(user);
        p.setName(dto.getName());
        p.setIsPublic(dto.getIsPublic() == null ? true : dto.getIsPublic());

        Playlist saved = playlistRepository.save(p);
        return PlaylistDto.from(saved);
    }


    @Override
    public List<PlaylistDto> getPlaylistsByUser() {
        var userId = currentUserProvider.get().getId();
        return playlistRepository.findByUserId(userId).stream()
                .map(PlaylistDto::from)
                .collect(Collectors.toList());
    }


    @Override
    public PlaylistDetailDto getPlaylistDetail(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        List<PlaylistSong> items = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        return PlaylistDetailDto.from(playlist, items);
    }



    @Override
    public void deletePlaylist(Long playlistId) {
        var userId = currentUserProvider.get().getId();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        playlistRepository.delete(playlist);
    }



    @Override
    public void addSongToPlaylist(Long playlistId, Long songId) {
        var userId = currentUserProvider.get().getId();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");

        songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        Optional<PlaylistSong> existing = playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId);
        if (existing.isPresent()) return; // idempotent

        // determine next position (append to end)
        List<PlaylistSong> items = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        int nextPos = items.isEmpty() ? 0 : items.get(items.size() - 1).getPosition() + 1;

        PlaylistSong ps = new PlaylistSong();
        ps.setPlaylist(playlist);
        ps.setSong(songRepository.getReferenceById(songId));
        ps.setPosition(nextPos);
        playlistSongRepository.save(ps);
    }


    @Override
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        var userId = currentUserProvider.get().getId();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");

        playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .ifPresent(ps -> {
                    int removedPosition = ps.getPosition();
                    playlistSongRepository.delete(ps);
                    // compact positions: shift down positions greater than removedPosition
                    List<PlaylistSong> items = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
                    for (int i = 0; i < items.size(); i++) {
                        PlaylistSong cur = items.get(i);
                        if (!cur.getPosition().equals(i)) {
                            cur.setPosition(i);
                            playlistSongRepository.save(cur);
                        }
                    }
                });
    }


        @Override
    public PlaylistDto updatePlaylist(Long playlistId, PlaylistDto dto) {
        var userId = currentUserProvider.get().getId();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        // Chỉ chủ sở hữu mới được sửa
        if (!playlist.getUser().getId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        // Update fields có trong DTO
        if (dto.getName() != null) {
            playlist.setName(dto.getName());
        }

        if (dto.getIsPublic() != null) {
            playlist.setIsPublic(dto.getIsPublic());
        }

        Playlist updated = playlistRepository.save(playlist);
        return PlaylistDto.from(updated);
    }

    @Override
    public void reorderPlaylist(Long playlistId, List<Long> orderedSongIds) {
        var userId = currentUserProvider.get().getId();
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (!playlist.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");

        List<PlaylistSong> current = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        // map songId -> PlaylistSong
        Map<Long, PlaylistSong> map = current.stream()
                .collect(Collectors.toMap(ps -> ps.getSong().getId(), ps -> ps));

        // apply new positions, only for songIds that exist in playlist
        for (int i = 0; i < orderedSongIds.size(); i++) {
            Long songId = orderedSongIds.get(i);
            PlaylistSong ps = map.get(songId);
            if (ps != null) {
                ps.setPosition(i);
                playlistSongRepository.save(ps);
            }
        }
    }
}
