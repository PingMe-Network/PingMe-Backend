package me.huynhducphu.PingMe_Backend.service.friendship.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.friendship.FriendInvitationRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.dto.response.friendship.HistoryFriendshipResponse;
import me.huynhducphu.PingMe_Backend.dto.response.friendship.UserFriendshipStatsResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.event.FriendshipEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.friendship.FriendshipEventType;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.constant.FriendshipStatus;
import me.huynhducphu.PingMe_Backend.model.Friendship;
import me.huynhducphu.PingMe_Backend.repository.FriendshipRepository;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Admin 8/19/2025
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class FriendshipServiceImpl implements me.huynhducphu.PingMe_Backend.service.friendship.FriendshipService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    private final CurrentUserProvider currentUserProvider;

    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void sendInvitation(FriendInvitationRequest friendInvitationRequest) {
        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        // Kiểm tra người gửi lời mời có phải chính mình không
        if (currentUser.getId().equals(friendInvitationRequest.getTargetUserId()))
            throw new IllegalArgumentException("Không thể kết bạn với chính mình");

        // Tìm thông tin người được mời kết bạn
        var targetUser = userRepository
                .findById(friendInvitationRequest.getTargetUserId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng cần kết bạn"));

        // Xây dựng cặp (lowId, highId) để đảm bảo uniqueness cho friendship
        Long lowId = Math.min(currentUser.getId(), targetUser.getId());
        Long highId = Math.max(currentUser.getId(), targetUser.getId());

        // Nếu đã tồn tại friendship với cặp id này → không cho tạo mới
        if (friendshipRepository.existsByUserLowIdAndUserHighId(lowId, highId))
            throw new DataIntegrityViolationException("Lời kết bạn đã tồn tại");

        // Tạo entity Friendship
        // - userA: người gửi lời mời
        // - userB: người nhận lời mời
        // - status: PENDING (chưa được chấp nhận/từ chối)
        // - userLowId/userHighId: id nhỏ/lớn hơn để tránh trùng
        var friendship = new Friendship();
        friendship.setUserA(currentUser);
        friendship.setUserB(targetUser);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);
        friendship.setUserLowId(lowId);
        friendship.setUserHighId(highId);

        // Lưu friendship
        friendshipRepository.save(friendship);

        // Bắn Event Websocket
        eventPublisher.publishEvent(new FriendshipEvent(
                FriendshipEventType.INVITED,
                friendship,
                friendship.getUserB().getId()
        ));
    }

    @Override
    public void acceptInvitation(Long friendRequestId) {
        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        // Tìm kiếm friendship dựa vào id
        var friendship = friendshipRepository
                .findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lời mời kết bạn này"));

        // Kiểm tra trạng thái friendship phải là PENDING mới được chấp nhận
        if (friendship.getFriendshipStatus() != FriendshipStatus.PENDING)
            throw new DataIntegrityViolationException("Trạng thái lời mời không thích hợp");

        // Xác minh người dùng hiện tại là người nhận lời mời
        if (!friendship.getUserB().getId().equals(currentUser.getId()))
            throw new DataIntegrityViolationException("Chỉ có người được nhận lời mời mới có thể chấp nhận");

        // Cập nhật trạng thái thành ACCEPTED
        friendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);

        // Bắn Event Websocket
        eventPublisher.publishEvent(new FriendshipEvent(
                FriendshipEventType.ACCEPTED,
                friendship,
                friendship.getUserA().getId()
        ));
    }

    @Override
    public void rejectInvitation(Long friendRequestId) {
        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        // Tìm kiếm friendship dựa vào id
        var friendship = friendshipRepository
                .findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lời mời kết bạn này"));

        // Kiểm tra trạng thái friendship phải là PENDING mới được chấp nhận
        if (friendship.getFriendshipStatus() != FriendshipStatus.PENDING)
            throw new IllegalArgumentException("Trạng thái lời mời không thích hợp");

        // Xác minh người dùng hiện tại là người nhận lời mời
        var isParticipant = friendship.getUserB().getId().equals(currentUser.getId());
        if (!isParticipant)
            throw new IllegalArgumentException("Chỉ có người được nhận lời mời mới có thể hủy");

        // Xoá friendship
        friendshipRepository.delete(friendship);

        // Bắn Event Websocket
        eventPublisher.publishEvent(new FriendshipEvent(
                FriendshipEventType.REJECTED,
                friendship,
                friendship.getUserA().getId()
        ));
    }

    @Override
    public void cancelInvitation(Long friendRequestId) {
        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        // Tìm kiếm friendship dựa vào id
        var friendship = friendshipRepository
                .findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lời mời kết bạn này"));

        // Kiểm tra trạng thái friendship phải là PENDING mới được chấp nhận
        if (friendship.getFriendshipStatus() != FriendshipStatus.PENDING)
            throw new IllegalArgumentException("Trạng thái lời mời không thích hợp");

        // Xác minh người dùng hiện tại là người gửi lời mời
        var isParticipant = friendship.getUserA().getId().equals(currentUser.getId());
        if (!isParticipant)
            throw new IllegalArgumentException("Chỉ có người được gửi lời mời mới có thể thu hồi");

        // Xoá friendship
        friendshipRepository.delete(friendship);

        // Bắn Event Websocket
        eventPublisher.publishEvent(new FriendshipEvent(
                FriendshipEventType.CANCELED,
                friendship,
                friendship.getUserB().getId()
        ));
    }

    @Override
    public void deleteFriendship(Long friendRequestId) {
        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        // Tìm kiếm friendship dựa vào id
        var friendship = friendshipRepository
                .findById(friendRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy mối quan hệ"));

        // Kiểm tra trạng thái friendship phải là ACCEPTED mới được chấp nhận
        if (friendship.getFriendshipStatus() != FriendshipStatus.ACCEPTED)
            throw new IllegalArgumentException("Trạng thái lời mời không thích hợp");

        // Xác minh người dùng hiện tại có thuộc về friendship
        var isParticipant = friendship.getUserA().getId().equals(currentUser.getId())
                || friendship.getUserB().getId().equals(currentUser.getId());
        if (!isParticipant)
            throw new IllegalArgumentException("Chỉ có người trong mối quan hệ này mới có thể xóa");

        // Xoá friendship
        friendshipRepository.delete(friendship);

        // Bắn Event Websocket
        var isUserA = friendship.getUserA().getId().equals(currentUser.getId());
        eventPublisher.publishEvent(new FriendshipEvent(
                FriendshipEventType.DELETED,
                friendship,
                isUserA ? friendship.getUserB().getId() : friendship.getUserA().getId()
        ));
    }

    @Override
    public HistoryFriendshipResponse getAcceptedFriendshipHistoryList(
            Long beforeId,
            Integer size
    ) {
        if (size == null)
            throw new IllegalArgumentException("Số lượng không hợp lệ");

        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        int fixedSize = Math.max(1, Math.min(size, 20));
        Pageable limit = PageRequest.of(0, fixedSize);

        Page<Friendship> page = friendshipRepository
                .findAllByStatusAndUserWithBeforeId(
                        FriendshipStatus.ACCEPTED, currentUser.getId(),
                        beforeId, limit
                );

        List<UserSummaryResponse> userSummaryResponses = page
                .getContent()
                .stream()
                .map(friendship -> {
                    var user = friendship.getUserA().getId().equals(currentUser.getId())
                            ? friendship.getUserB()
                            : friendship.getUserA();
                    return mapToDto(user, friendship);
                })
                .toList();

        return new HistoryFriendshipResponse(userSummaryResponses, page.getTotalElements());
    }

    @Override
    public HistoryFriendshipResponse getReceivedHistoryInvitations(
            Long beforeId,
            Integer size
    ) {
        if (size == null)
            throw new IllegalArgumentException("Số lượng không hợp lệ");

        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        int fixedSize = Math.max(1, Math.min(size, 20));
        Pageable limit = PageRequest.of(0, fixedSize);

        Page<Friendship> page = friendshipRepository
                .findByStatusAndUserB_IdWithBeforeId(
                        FriendshipStatus.PENDING, currentUser.getId(),
                        beforeId, limit
                );

        List<UserSummaryResponse> userSummaryResponses = page
                .getContent()
                .stream()
                .map(friendship -> {
                    var invitee = friendship.getUserA();
                    return mapToDto(invitee, friendship);
                })
                .toList();

        return new HistoryFriendshipResponse(userSummaryResponses, page.getTotalElements());
    }

    @Override
    public HistoryFriendshipResponse getSentHistoryInvitations(
            Long beforeId,
            Integer size
    ) {
        if (size == null)
            throw new IllegalArgumentException("Số lượng không hợp lệ");

        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        int fixedSize = Math.max(1, Math.min(size, 20));
        Pageable limit = PageRequest.of(0, fixedSize);

        Page<Friendship> page = friendshipRepository
                .findByStatusAndUserA_IdWithBeforeId(
                        FriendshipStatus.PENDING, currentUser.getId(),
                        beforeId, limit
                );
        List<UserSummaryResponse> userSummaryResponses = page
                .getContent()
                .stream()
                .map(friendship -> {
                    var invitee = friendship.getUserB();
                    return mapToDto(invitee, friendship);
                })
                .toList();

        return new HistoryFriendshipResponse(userSummaryResponses, page.getTotalElements());
    }

    @Override
    public UserFriendshipStatsResponse getUserFrendshipStats() {
        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        return friendshipRepository.getStats(currentUser.getId());
    }

    // =====================================
    // Utilities methods
    // =====================================
    private UserSummaryResponse mapToDto(User user, Friendship friendship) {
        var userSummaryResponse = modelMapper.map(user, UserSummaryResponse.class);
        var friendshipSummary = modelMapper.map(friendship, UserSummaryResponse.FriendshipSummary.class);
        userSummaryResponse.setFriendshipSummary(friendshipSummary);

        return userSummaryResponse;
    }


}
