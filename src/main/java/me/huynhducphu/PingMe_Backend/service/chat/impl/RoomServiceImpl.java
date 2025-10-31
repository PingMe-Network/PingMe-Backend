package me.huynhducphu.PingMe_Backend.service.chat.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.chat.room.CreateOrGetDirectRoomRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.room.RoomResponse;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;
import me.huynhducphu.PingMe_Backend.model.common.RoomMemberId;
import me.huynhducphu.PingMe_Backend.model.constant.RoomRole;
import me.huynhducphu.PingMe_Backend.model.constant.RoomType;
import me.huynhducphu.PingMe_Backend.repository.RoomParticipantRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomRepository;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.chat.util.ChatDtoUtils;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin 8/25/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements me.huynhducphu.PingMe_Backend.service.chat.RoomService {

    private final CurrentUserProvider currentUserProvider;

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final UserRepository userRepository;

    @Override
    public RoomResponse createOrGetDirectRoom(CreateOrGetDirectRoomRequest createOrGetDirectRoomRequest) {
        var currentUser = currentUserProvider.get();

        if (currentUser.getId().equals(createOrGetDirectRoomRequest.getTargetUserId()))
            throw new IllegalArgumentException("Bạn không thể tự nhắn tin cho chính mình");

        if (!userRepository.existsById(createOrGetDirectRoomRequest.getTargetUserId()))
            throw new IllegalArgumentException("Người dùng cần nhắn tin không tồn tại");

        String directKey = buildDirectKey(currentUser.getId(), createOrGetDirectRoomRequest.getTargetUserId());

        var room = roomRepository.findByDirectKey(directKey).orElse(null);

        if (room != null) {
            ensureParticipants(room, currentUser.getId(), createOrGetDirectRoomRequest.getTargetUserId());
            return ChatDtoUtils.toRoomResponseDto(
                    room,
                    roomParticipantRepository.findByRoom_Id(room.getId()),
                    currentUser.getId()
            );
        }

        try {
            var newRoom = new Room();

            newRoom.setRoomType(RoomType.DIRECT);
            newRoom.setDirectKey(directKey);
            newRoom.setName(null);
            newRoom.setLastMessage(null);
            newRoom.setLastMessageAt(null);

            var savedRoom = roomRepository.save(newRoom);

            addParticipant(savedRoom, currentUser.getId());
            addParticipant(savedRoom, createOrGetDirectRoomRequest.getTargetUserId());

            return ChatDtoUtils.toRoomResponseDto(
                    savedRoom,
                    roomParticipantRepository.findByRoom_Id(savedRoom.getId()),
                    currentUser.getId()
            );
        } catch (DataIntegrityViolationException ex) {
            Room existed = roomRepository.findByDirectKey(directKey).orElseThrow(() -> ex);
            ensureParticipants(existed, currentUser.getId(), createOrGetDirectRoomRequest.getTargetUserId());
            return ChatDtoUtils.toRoomResponseDto(existed, roomParticipantRepository.findByRoom_Id(existed.getId()), currentUser.getId());
        }
    }

    @Override
    public Page<RoomResponse> getCurrentUserRooms(Pageable pageable) {
        var currentUser = currentUserProvider.get();
        var currentUserId = currentUser.getId();

        Page<Room> page = roomRepository.findAllByMember(currentUserId, pageable);

        if (page.isEmpty()) return Page.empty(pageable);

        List<Long> roomIds = page.getContent().stream().map(Room::getId).toList();
        Map<Long, List<RoomParticipant>> participantsByRoom = roomParticipantRepository
                .findByRoom_IdIn(roomIds)
                .stream()
                .collect(Collectors.groupingBy(rp -> rp.getRoom().getId()));


        List<RoomResponse> content = page
                .getContent()
                .stream()
                .map(room -> {
                    var members = participantsByRoom.getOrDefault(room.getId(), List.of());
                    return ChatDtoUtils.toRoomResponseDto(room, members, currentUserId);
                })
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    // =====================================
    // Utilities methods
    // =====================================
    private String buildDirectKey(Long a, Long b) {
        long low = Math.min(a, b);
        long high = Math.max(a, b);
        return low + "_" + high;
    }

    private void ensureParticipants(Room room, Long currentUserId, Long targetUserId) {
        addParticipant(room, currentUserId);
        addParticipant(room, targetUserId);
    }

    private void addParticipant(Room room, Long userId) {
        RoomMemberId pk = new RoomMemberId(room.getId(), userId);
        if (roomParticipantRepository.existsById(pk)) return;

        RoomParticipant rp = new RoomParticipant();
        rp.setId(pk);
        rp.setRoom(room);
        rp.setUser(userRepository.getReferenceById(userId));
        rp.setRole(RoomRole.MEMBER);

        try {
            roomParticipantRepository.save(rp);
        } catch (DataIntegrityViolationException ignored) {

        }
    }

}
