package me.huynhducphu.PingMe_Backend.service.chat;

import me.huynhducphu.PingMe_Backend.dto.request.chat.room.CreateOrGetDirectRoomRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin 8/25/2025
 *
 **/
public interface RoomService {
    RoomResponse createOrGetDirectRoom(CreateOrGetDirectRoomRequest createOrGetDirectRoomRequest);

    Page<RoomResponse> getCurrentUserRooms(Pageable pageable);
}
