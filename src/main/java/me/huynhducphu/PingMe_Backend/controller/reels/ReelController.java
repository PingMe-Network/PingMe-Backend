package me.huynhducphu.PingMe_Backend.controller.reels;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.reels.CreateReelRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelResponse;
import me.huynhducphu.PingMe_Backend.service.reels.ReelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Reels", description = "Các endpoints reels")
@RestController
@RequestMapping("/reels")
@RequiredArgsConstructor
public class ReelController {

    private final ReelService reelService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ReelResponse>> createReel(
            @RequestPart("data") String dataJson,
            @RequestPart("video") MultipartFile video
    ) throws Exception {
        CreateReelRequest data = objectMapper.readValue(dataJson, CreateReelRequest.class);
        var res = reelService.createReel(data, video);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(res));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<PageResponse<ReelResponse>>> getFeed(
            @PageableDefault Pageable pageable
    ) {
        var page = reelService.getFeed(pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    // user xem reel -> +1 view
    @PostMapping("/{reelId}/views")
    public ResponseEntity<ApiResponse<ReelResponse>> addView(@PathVariable Long reelId) {
        var res = reelService.incrementView(reelId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    // like/unlike
    @PostMapping("/{reelId}/likes/toggle")
    public ResponseEntity<ApiResponse<ReelResponse>> toggleLike(@PathVariable Long reelId) {
        var res = reelService.toggleLike(reelId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @DeleteMapping("/{reelId}")
    public ResponseEntity<ApiResponse<Void>> deleteReel(@PathVariable Long reelId) {
        reelService.deleteReel(reelId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }
}
