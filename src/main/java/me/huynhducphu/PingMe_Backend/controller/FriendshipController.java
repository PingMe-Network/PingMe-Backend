package me.huynhducphu.PingMe_Backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.friendship.FriendInvitationRequest;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.dto.response.friendship.HistoryFriendshipResponse;
import me.huynhducphu.PingMe_Backend.service.friendship.FriendshipService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/19/2025
 **/
@Tag(
        name = "Friendships",
        description = "Các endpoints gửi/nhận lời mời kết bạn và quản lý danh sách bạn bè"
)
@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendInvitation(
            @RequestBody FriendInvitationRequest friendInvitationRequest
    ) {
        friendshipService.sendInvitation(friendInvitationRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>());
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(@PathVariable Long id) {
        friendshipService.acceptInvitation(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>());
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInvitation(@PathVariable Long id) {
        friendshipService.rejectInvitation(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>());
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(@PathVariable Long id) {
        friendshipService.cancelInvitation(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFriendship(@PathVariable Long id) {
        friendshipService.deleteFriendship(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>());
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<HistoryFriendshipResponse>> getAcceptedFriendshipHistoryList(
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        var res = friendshipService.getAcceptedFriendshipHistoryList(
                beforeId,
                size
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(res));
    }

    @GetMapping("/history/received")
    public ResponseEntity<ApiResponse<HistoryFriendshipResponse>> getReceivedHistoryInvitations(
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        var res = friendshipService.getReceivedHistoryInvitations(
                beforeId,
                size
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(res));
    }

    @GetMapping("/history/sent")
    public ResponseEntity<ApiResponse<HistoryFriendshipResponse>> getSentHistoryInvitations(
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        var res = friendshipService.getSentHistoryInvitations(
                beforeId,
                size
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(res));
    }

}
