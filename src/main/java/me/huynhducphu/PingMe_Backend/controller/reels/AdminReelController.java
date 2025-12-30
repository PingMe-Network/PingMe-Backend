package me.huynhducphu.PingMe_Backend.controller.reels;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.reels.AdminReelFilterRequest;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.base.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.reels.AdminReelResponse;
import me.huynhducphu.PingMe_Backend.service.reels.AdminReelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Reels", description = "Admin quản lý reels")
@RestController
@RequestMapping("/admin/reels")
@RequiredArgsConstructor
public class AdminReelController {

    private final AdminReelService adminReelService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminReelResponse>>> getReels(
            @ModelAttribute AdminReelFilterRequest filter,
            @PageableDefault Pageable pageable
    ) {
        System.out.println("FILTER = " + filter);
        var page = adminReelService.getReels(filter, pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    @GetMapping("/{reelId}")
    public ResponseEntity<ApiResponse<AdminReelResponse>> getDetail(
            @PathVariable Long reelId
    ) {
        var res = adminReelService.getDetail(reelId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @PatchMapping("/{reelId}/caption")
    public ResponseEntity<ApiResponse<AdminReelResponse>> updateCaption(
            @PathVariable Long reelId,
            @RequestBody String caption
    ) {
        var res = adminReelService.updateCaption(reelId, caption);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @PatchMapping("/{reelId}/hide")
    public ResponseEntity<ApiResponse<AdminReelResponse>> hide(
            @PathVariable Long reelId,
            @RequestParam(required = false) String note
    ) {
        var res = adminReelService.hideReel(reelId, note);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @DeleteMapping("/{reelId}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDelete(
            @PathVariable Long reelId
    ) {
        adminReelService.hardDelete(reelId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }
}
