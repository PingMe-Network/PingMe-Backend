package me.huynhducphu.PingMe_Backend.controller.music;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.FavoriteDto;
import me.huynhducphu.PingMe_Backend.service.music.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getFavorites());
    }

    @PostMapping("/{songId}")
    public ResponseEntity<Void> addFav(@PathVariable Long songId) {
        favoriteService.addFavorite(songId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> removeFav(@PathVariable Long songId) {
        favoriteService.removeFavorite(songId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/is/{songId}")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long songId){
        return ResponseEntity.ok(favoriteService.isFavorite(songId));
    }
}
