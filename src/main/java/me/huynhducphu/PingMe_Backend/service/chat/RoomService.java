package me.huynhducphu.PingMe_Backend.service.chat;

import me.huynhducphu.PingMe_Backend.dto.request.chat.room.AddGroupMembersRequest;
import me.huynhducphu.PingMe_Backend.dto.request.chat.room.CreateGroupRoomRequest;
import me.huynhducphu.PingMe_Backend.dto.request.chat.room.CreateOrGetDirectRoomRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.model.constant.RoomRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin 8/25/2025
 *
 **/
public interface RoomService {
    RoomResponse createOrGetDirectRoom(CreateOrGetDirectRoomRequest createOrGetDirectRoomRequest);

    RoomResponse createGroupRoom(CreateGroupRoomRequest createGroupRoomRequest);

    RoomResponse addGroupMembers(AddGroupMembersRequest request);

    RoomResponse removeGroupMember(Long roomId, Long targetUserId);

    RoomResponse changeMemberRole(Long roomId, Long targetUserId, RoomRole newRole);

    Page<RoomResponse> getCurrentUserRooms(Pageable pageable);
}
