package me.huynhducphu.PingMe_Backend.controller.music;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.music.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/search/{title}")
    public ResponseEntity<List<SongResponse>> getSongByTitle(@PathVariable String title) {
        List<SongResponse> songResponses = songService.getSongByTitle(title);
        return ResponseEntity.ok(songResponses);
    }

    @GetMapping("/getTopSong/{number}")
    public ResponseEntity<List<SongResponse>> getAllSongs(@PathVariable int number) {
        List<SongResponse> songResponses = songService.getTopPlayedSongs(number);
        return ResponseEntity.ok(songResponses);
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
