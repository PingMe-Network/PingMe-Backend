package me.huynhducphu.PingMe_Backend.service.friendship;

import me.huynhducphu.PingMe_Backend.dto.request.friendship.FriendInvitationRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.UserSummaryResponse;
import me.huynhducphu.PingMe_Backend.dto.response.friendship.HistoryFriendshipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Admin 8/19/2025
 **/
public interface FriendshipService {
    void sendInvitation(FriendInvitationRequest friendInvitationRequest);

    void acceptInvitation(Long friendRequestId);

    void rejectInvitation(Long friendRequestId);

    void cancelInvitation(Long friendRequestId);

    void deleteFriendship(Long friendRequestId);

    HistoryFriendshipResponse getAcceptedFriendshipHistoryList(
            Long beforeId,
            Integer size
    );

    HistoryFriendshipResponse getReceivedHistoryInvitations(
            Long beforeId,
            Integer size
    );

    HistoryFriendshipResponse getSentHistoryInvitations(
            Long beforeId,
            Integer size
    );
}
