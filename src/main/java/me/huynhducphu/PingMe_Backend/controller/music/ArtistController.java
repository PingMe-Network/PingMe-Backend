package me.huynhducphu.PingMe_Backend.controller.music;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.music.ArtistRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.ArtistResponse;
import me.huynhducphu.PingMe_Backend.service.music.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 25/11/2025 - 11:32 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.controller.music
 */

@RestController
@RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/all")
    public ResponseEntity<List<ArtistResponse>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistResponse>> searchArtists(@RequestParam String name) {
        return ResponseEntity.ok(artistService.searchArtists(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<ArtistResponse> saveArtist(
            @Valid @RequestPart("artistRequest") ArtistRequest artistRequest,
            @RequestPart(value = "imgFile") MultipartFile imgFile
    ){
        return ResponseEntity.ok(artistService.saveArtist(artistRequest, imgFile));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ArtistResponse> updateArtist(
            @PathVariable Long id,
            @Valid @RequestPart("artistRequest") ArtistRequest artistRequest,
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile
    ) {
        return ResponseEntity.ok(artistService.updateArtist(id, artistRequest, imgFile));
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDeleteArtist(@PathVariable Long id) {
        artistService.softDeleteArtist(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteArtist(@PathVariable Long id) {
        artistService.hardDeleteArtist(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreArtist(@PathVariable Long id) {
        artistService.restoreArtist(id);
        return ResponseEntity.ok().build();
    }
}
