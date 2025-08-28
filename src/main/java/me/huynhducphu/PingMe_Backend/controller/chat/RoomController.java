package me.huynhducphu.PingMe_Backend.controller.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.chat.room.CreateOrGetDirectRoomRequest;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.PageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.service.chat.RoomService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 8/26/2025
 *
 **/
@Tag(
        name = "Rooms",
        description = "Các endpoints xử lý phòng chat"
)
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/direct")
    public ResponseEntity<ApiResponse<RoomResponse>> createOrGetDirectRoom(
            @RequestBody @Valid CreateOrGetDirectRoomRequest createOrGetDirectRoomRequest
    ) {
        return ResponseEntity
                .ok(new ApiResponse<>(roomService.createOrGetDirectRoom(createOrGetDirectRoomRequest)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> getCurrentUserRooms(
            @PageableDefault Pageable pageable
    ) {
        var page = roomService.getCurrentUserRooms(pageable);
        var pageResponse = new PageResponse<>(page);

        return ResponseEntity.ok(new ApiResponse<>(pageResponse));
    }


}
