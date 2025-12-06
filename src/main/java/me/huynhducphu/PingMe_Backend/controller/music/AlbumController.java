package me.huynhducphu.PingMe_Backend.controller.music;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.music.AlbumRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.AlbumResponse;
import me.huynhducphu.PingMe_Backend.service.music.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long id){
        AlbumResponse albumResponse = albumService.getAlbumById(id);
        return ResponseEntity.ok(albumResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumResponse>> searchAlbums(@RequestParam String title){
        List<AlbumResponse> albumResponses = albumService.getAlbumByTitleContainIgnoreCase(title);
        return ResponseEntity.ok(albumResponses);
    }

    @PostMapping("/save")
    public ResponseEntity<AlbumResponse> save (
            @Valid @RequestPart("albumRequest") AlbumRequest albumRequest,
            @RequestPart(value = "albumCoverImg") MultipartFile albumCoverImg
    ){
        AlbumResponse albumResponse = albumService.save(albumRequest, albumCoverImg);

        return ResponseEntity.ok(albumResponse);
    }

    @PutMapping("/update/{albumId}")
    public ResponseEntity<AlbumResponse> update(
            @PathVariable Long albumId,
            @Valid @RequestPart("albumRequest") AlbumRequest albumRequestDto,
            @RequestPart(value = "albumCoverImg") MultipartFile albumCoverImg
    ){
        AlbumResponse albumResponse = albumService.update(albumId, albumRequestDto, albumCoverImg);

        return ResponseEntity.ok(albumResponse);
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id){
        albumService.softDelete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id){
        albumService.hardDelete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        albumService.restore(id);
        return ResponseEntity.ok().build();
    }
}
