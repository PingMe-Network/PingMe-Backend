package me.huynhducphu.PingMe_Backend.controller.reels;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.reels.UpsertReelCommentRequest;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.base.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelCommentResponse;
import me.huynhducphu.PingMe_Backend.service.reels.ReelCommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reel Comments", description = "Endpoints bình luận reels")
@RestController
@RequestMapping("/reel-comments")
@RequiredArgsConstructor
public class ReelCommentController {

    private final ReelCommentService reelCommentService;

    @PostMapping("/reels/{reelId}")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> create(
            @PathVariable Long reelId,
            @Valid @RequestBody UpsertReelCommentRequest dto
    ) {
        var res = reelCommentService.createComment(reelId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(res));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> update(
            @PathVariable Long commentId,
            @Valid @RequestBody UpsertReelCommentRequest dto
    ) {
        // Verify the current authenticated user is the owner of the comment
        if (!reelCommentService.isCommentOwner(commentId)) {
            throw new AccessDeniedException("Bạn không có quyền sửa bình luận này");
        }
        var res = reelCommentService.updateComment(commentId, dto);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @GetMapping("/reels/{reelId}")
    public ResponseEntity<ApiResponse<PageResponse<ReelCommentResponse>>> getByReel(
            @PathVariable Long reelId,
            @PageableDefault Pageable pageable
    ) {
        var page = reelCommentService.getComments(reelId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long commentId) {
        reelCommentService.deleteComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<ApiResponse<PageResponse<ReelCommentResponse>>> getReplies(
            @PathVariable Long commentId,
            @PageableDefault Pageable pageable
    ) {
        var page = reelCommentService.getReplies(commentId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(new PageResponse<>(page)));
    }

    @PostMapping("/{commentId}/pin")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> pin(@PathVariable Long commentId) {
        var res = reelCommentService.pinComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @PostMapping("/{commentId}/unpin")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> unpin(@PathVariable Long commentId) {
        var res = reelCommentService.unpinComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @PostMapping("/{commentId}/reactions")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> react(
            @PathVariable Long commentId,
            @RequestParam("type") me.huynhducphu.PingMe_Backend.model.constant.ReactionType type
    ) {
        var res = reelCommentService.react(commentId, type);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }

    @DeleteMapping("/{commentId}/reactions")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> unreact(
            @PathVariable Long commentId
    ) {
        var res = reelCommentService.unreact(commentId);
        return ResponseEntity.ok(new ApiResponse<>(res));
    }
}
