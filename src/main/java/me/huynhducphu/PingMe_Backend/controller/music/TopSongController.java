package me.huynhducphu.PingMe_Backend.controller.music;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.TopSongPlayCounterDto;
import me.huynhducphu.PingMe_Backend.service.music.SongPlayHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/top-songs")
@RequiredArgsConstructor
public class TopSongController {
    private final SongPlayHistoryService songPlayHistoryService;

    @GetMapping("/today")
    public ResponseEntity<List<TopSongPlayCounterDto>> getTopSongsToday(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<TopSongPlayCounterDto> topSongs = songPlayHistoryService.getTopSongsTodayCached(limit);
        return ResponseEntity.ok(topSongs);
    }

    @GetMapping("/week")
    public ResponseEntity<List<TopSongPlayCounterDto>> getTopSongsThisWeek(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<TopSongPlayCounterDto> topSongs = songPlayHistoryService.getTopSongsThisWeekCached(limit);
        return ResponseEntity.ok(topSongs);
    }

    @GetMapping("/month")
    public ResponseEntity<List<TopSongPlayCounterDto>> getTopSongsThisMonth(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<TopSongPlayCounterDto> topSongs = songPlayHistoryService.getTopSongsThisMonthCached(limit);
        return ResponseEntity.ok(topSongs);
    }
}
