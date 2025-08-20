package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.constant.FriendshipStatus;
import me.huynhducphu.PingMe_Backend.model.user.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Admin 8/19/2025
 **/
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Page<Friendship> findAllByFriendshipStatusAndUserA_IdOrFriendshipStatusAndUserB_Id(
            FriendshipStatus status1, Long userId1,
            FriendshipStatus status2, Long userId2,
            Pageable pageable
    );

    boolean existsByUserLowIdAndUserHighId(Long userLowId, Long userHighId);

    Page<Friendship> findByFriendshipStatusAndUserA_Id(FriendshipStatus status, Long userId, Pageable pageable);

    Page<Friendship> findByFriendshipStatusAndUserB_Id(FriendshipStatus status, Long userId, Pageable pageable);
}
