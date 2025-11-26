package me.huynhducphu.PingMe_Backend.controller.music;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.music.GenreRequest;
import me.huynhducphu.PingMe_Backend.dto.response.music.GenreResponse;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.GenreDto;
import me.huynhducphu.PingMe_Backend.service.music.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 23/11/2025 - 6:09 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.controller.music
 */

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/all")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable Long id){
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<GenreResponse> saveGenre(
            @Valid @RequestPart("genreRequest") GenreRequest genreRequest
    ){
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable Long id, GenreRequest request
    ) {
        return ResponseEntity.ok(genreService.updateGenre(id, request));
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDeleteGenre(@PathVariable Long id) {
        genreService.softDeleteGenre(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restoreGenre(@PathVariable Long id) {
        genreService.restoreGenre(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteGenre(@PathVariable Long id) {
        genreService.hardDeleteGenre(id);
        return ResponseEntity.ok().build();
    }
}
