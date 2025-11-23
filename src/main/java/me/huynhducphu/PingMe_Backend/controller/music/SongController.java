package me.huynhducphu.PingMe_Backend.controller.music;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.SongResponse;
import me.huynhducphu.PingMe_Backend.service.music.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

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

}
