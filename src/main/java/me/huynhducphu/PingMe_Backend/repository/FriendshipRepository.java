package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.constant.FriendshipStatus;
import me.huynhducphu.PingMe_Backend.model.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Admin 8/19/2025
 **/
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    boolean existsByUserLowIdAndUserHighId(Long userLowId, Long userHighId);

    Optional<Friendship> findByUserLowIdAndUserHighId(Long userLowId, Long userHighId);
    
    @Query("""
                SELECT f
                FROM Friendship f
                WHERE f.friendshipStatus = :status
                  AND f.userA.id = :userId
                  AND (:beforeId IS NULL OR f.id < :beforeId)
                ORDER BY f.id DESC
            """)
    Page<Friendship> findByStatusAndUserA_IdWithBeforeId(
            @Param("status") FriendshipStatus status,
            @Param("userId") Long userId,
            @Param("beforeId") Long beforeId,
            Pageable pageable
    );


    @Query("""
                SELECT f
                FROM Friendship f
                WHERE f.friendshipStatus = :status
                  AND f.userB.id = :userId
                  AND (:beforeId IS NULL OR f.id < :beforeId)
                ORDER BY f.id DESC
            """)
    Page<Friendship> findByStatusAndUserB_IdWithBeforeId(
            @Param("status") FriendshipStatus status,
            @Param("userId") Long userId,
            @Param("beforeId") Long beforeId,
            Pageable pageable
    );


    @Query("""
                SELECT f
                FROM Friendship f
                WHERE f.friendshipStatus = :status
                  AND (
                    (f.userA.id = :userId)
                    OR (f.userB.id = :userId)
                  )
                  AND (:beforeId IS NULL OR f.id < :beforeId)
                ORDER BY f.id DESC
            """)
    Page<Friendship> findAllByStatusAndUserWithBeforeId(
            @Param("status") FriendshipStatus status,
            @Param("userId") Long userId,
            @Param("beforeId") Long beforeId,
            Pageable pageable
    );


}
