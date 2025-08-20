package me.huynhducphu.PingMe_Backend.controller;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.friendship.FriendInvitationRequest;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.service.friendship.FriendshipService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/19/2025
 **/
@RestController
@RequestMapping("/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PostMapping
    public void sendInvitation(@RequestBody FriendInvitationRequest friendInvitationRequest) {
        friendshipService.sendInvitation(friendInvitationRequest);
    }

    @PostMapping("/{id}/accept")
    public void acceptInvitation(@PathVariable Long id) {
        friendshipService.acceptInvitation(id);
    }

    @DeleteMapping("/{id}/reject")
    public void rejectInvitation(@PathVariable Long id) {
        friendshipService.rejectInvitation(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFriendship(@PathVariable Long id) {
        friendshipService.deleteFriendship(id);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getAcceptedFriendshipList(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        var page = friendshipService.getAcceptedFriendshipList(pageable);
        var pageResponse = new PageResponse<>(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(pageResponse));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getReceivedInvitations(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        var page = friendshipService.getReceivedInvitations(pageable);
        var pageResponse = new PageResponse<>(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(pageResponse));
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getSentInvitations(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        var page = friendshipService.getSentInvitations(pageable);
        var pageResponse = new PageResponse<>(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(pageResponse));
    }

}
