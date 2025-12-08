package me.huynhducphu.PingMe_Backend.repository.chat;

import me.huynhducphu.PingMe_Backend.model.chat.RoomParticipant;
import me.huynhducphu.PingMe_Backend.model.common.RoomMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Admin 8/25/2025
 *
 **/
@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, RoomMemberId> {
    List<RoomParticipant> findByRoom_Id(Long roomId);

    List<RoomParticipant> findByRoom_IdIn(List<Long> roomIds);
}
