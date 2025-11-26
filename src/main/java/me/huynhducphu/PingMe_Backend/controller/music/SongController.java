package me.huynhducphu.PingMe_Backend.controller.music;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.music.SongRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;

import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponseWithAllAlbum;

import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;

import me.huynhducphu.PingMe_Backend.service.music.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongDetail(@PathVariable Long id) {
        SongResponse songResponse = songService.getSongById(id);
        return ResponseEntity.ok(songResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SongResponseWithAllAlbum>> getAllSongs() {
        List<SongResponseWithAllAlbum> songResponseWithAllAlbums = songService.getAllSongs();
        return ResponseEntity.ok(songResponseWithAllAlbums);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongResponse>> getSongByTitle(@RequestParam("title") String title) {
        List<SongResponse> songResponses = songService.getSongByTitle(title);
        return ResponseEntity.ok(songResponses);
    }

    @GetMapping("/getTopSong/{number}")
    public ResponseEntity<List<SongResponse>> getAllSongs(@PathVariable int number) {
        List<SongResponse> songResponses = songService.getTopPlayedSongs(number);
        return ResponseEntity.ok(songResponses);
    }

    @GetMapping("/genre")
    public ResponseEntity<List<SongResponse>> getByGenre(@RequestParam("id") Long genreId) {
        return ResponseEntity.ok(songService.getSongByGenre(genreId));
    }

    @PostMapping("/save")
    public ResponseEntity<List<SongResponse>> save(
            @Valid @RequestPart("songRequest") SongRequest songRequest,
            @RequestPart(value = "musicFile") MultipartFile musicFile,
            @RequestPart(value = "imgFile") MultipartFile imgFile
    ) {
        List<SongResponse> songResponses = songService.save(songRequest, musicFile, imgFile);
        return ResponseEntity.ok(songResponses);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<List<SongResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("songRequest") SongRequest songRequest,
            @RequestPart(value = "musicFile", required = false) MultipartFile musicFile,
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile
    ) {
        List<SongResponse> songResponses = songService.update(id, songRequest, musicFile, imgFile);
        return ResponseEntity.ok(songResponses);
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        songService.softDelete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        songService.hardDelete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        songService.restore(id);
    }
  
    @PostMapping("/{id}/play")
    public ResponseEntity<Void> increasePlayCount(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt // inject JWT nguyên
    ) {
        if (jwt == null) return ResponseEntity.status(401).build();

        // Lấy email từ "sub"
        String userSub = jwt.getClaimAsString("sub");
        if (userSub == null || userSub.isBlank()) return ResponseEntity.status(401).build();

        // Map email → userId
        User user = userRepository.getUserByEmail(userSub).orElse(null);
        Long userId = user.getId();
        if (userId == null) return ResponseEntity.status(401).build();

        songService.increasePlayCount(id, userId);
        return ResponseEntity.ok().build();
    }

}
