package me.huynhducphu.PingMe_Backend.controller.music;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDetailDto;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.PlaylistDto;
import me.huynhducphu.PingMe_Backend.service.music.PlaylistService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(@RequestBody PlaylistDto dto) {
        return ResponseEntity.ok(playlistService.createPlaylist(dto));
    }

    @GetMapping
    public ResponseEntity<List<PlaylistDto>> getPlaylists() {
        return ResponseEntity.ok(playlistService.getPlaylistsByUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDetailDto> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.getPlaylistDetail(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> addSong(@PathVariable Long id, @PathVariable Long songId) {
        boolean added = playlistService.addSongToPlaylist(id, songId);
        return ResponseEntity.ok(Map.of("alreadyExists", !added));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Void> removeSong(@PathVariable Long id, @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(id, songId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/songs/reorder")
    public ResponseEntity<Void> reorder(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        // payload example: { "orderedSongIds": [1,2,3,4] }
        @SuppressWarnings("unchecked")
        List<Integer> arr = (List<Integer>) payload.get("orderedSongIds");
        List<Long> ordered = arr.stream().map(Integer::longValue).toList();
        playlistService.reorderPlaylist(id, ordered);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDto> update(
            @PathVariable Long id,
            @RequestBody PlaylistDto dto
    ) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, dto));
    }

    @GetMapping("/public")
    public ResponseEntity<Page<PlaylistDto>> getPublicPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(playlistService.getPublicPlaylists(page, size));
    }


}
