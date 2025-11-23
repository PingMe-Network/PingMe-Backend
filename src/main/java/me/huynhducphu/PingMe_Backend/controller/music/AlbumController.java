package me.huynhducphu.PingMe_Backend.controller.music;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.AlbumResponse;
import me.huynhducphu.PingMe_Backend.service.music.AlbumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 23/11/2025 - 5:40 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.controller.music
 */

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping("/all")
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        List<AlbumResponse> albumResponses = albumService.getAllAlbums();
        return ResponseEntity.ok(albumResponses);
    }
}
