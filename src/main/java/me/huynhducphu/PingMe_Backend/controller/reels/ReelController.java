package me.huynhducphu.PingMe_Backend.controller.reels;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.reels.ReelRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelResponse;
import me.huynhducphu.PingMe_Backend.service.reels.ReelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        ReelRequest data = objectMapper.readValue(dataJson, ReelRequest.class);
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ReelResponse>>> search(
            @RequestParam(value = "query", required = false) String query,
            @PageableDefault Pageable pageable
    ) {
        var page = reelService.searchByTitle(query, pageable);
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

    // save/unsave
    @PostMapping("/{reelId}/saves/toggle")
    public ResponseEntity<ApiResponse<ReelResponse>> toggleSave(@PathVariable Long reelId) {
        var res = reelService.toggleSave(reelId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @DeleteMapping("/{reelId}")
    public ResponseEntity<ApiResponse<Void>> deleteReel(@PathVariable Long reelId) {
        reelService.deleteReel(reelId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }

    @PutMapping(value = "/{reelId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ReelResponse>> updateReel(
            @PathVariable Long reelId,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) throws Exception {
        ReelRequest data =
                objectMapper.readValue(dataJson, ReelRequest.class);

        var res = reelService.updateReel(reelId, data, video);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @GetMapping("/me/likes")
    public ResponseEntity<ApiResponse<PageResponse<ReelResponse>>> getMyLikedReels(
            @PageableDefault Pageable pageable
    ) {
        var page = reelService.getLikedReels(pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    @GetMapping("/me/saved")
    public ResponseEntity<ApiResponse<PageResponse<ReelResponse>>> getMySavedReels(
            @PageableDefault Pageable pageable
    ) {
        var page = reelService.getSavedReels(pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    @GetMapping("/me/views")
    public ResponseEntity<ApiResponse<PageResponse<ReelResponse>>> getMyViewedReels(
            @PageableDefault Pageable pageable
    ) {
        var page = reelService.getViewedReels(pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    @GetMapping("/me/marked")
    public ResponseEntity<ApiResponse<PageResponse<ReelResponse>>> getMyMarkedReels(
            @RequestParam(value = "type", defaultValue = "both") String type,
            @PageableDefault Pageable pageable
    ) {
        if ("liked".equalsIgnoreCase(type)) {
            var page = reelService.getLikedReels(pageable);
            return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
        }

        if ("saved".equalsIgnoreCase(type)) {
            var page = reelService.getSavedReels(pageable);
            return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
        }

        // both: merge liked and saved, de-duplicate by id, preserve liked first order then saved
        Page<ReelResponse> likedPage = reelService.getLikedReels(pageable);
        Page<ReelResponse> savedPage = reelService.getSavedReels(pageable);

        List<ReelResponse> merged = likedPage.getContent().stream()
                .collect(Collectors.toList());

        Map<Long, ReelResponse> byId = new LinkedHashMap<>();
        for (ReelResponse r : merged) byId.put(r.getId(), r);
        for (ReelResponse r : savedPage.getContent()) byId.putIfAbsent(r.getId(), r);

        List<ReelResponse> finalList = byId.values().stream().collect(Collectors.toList());

        // create a PageImpl with the pageable and combined total (approx) as sum of both distinct sizes
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), finalList.size());
        List<ReelResponse> pageContent = start > end ? List.of() : finalList.subList(start, end);
        Page<ReelResponse> page = new PageImpl<>(pageContent, pageable, finalList.size());

        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

}
