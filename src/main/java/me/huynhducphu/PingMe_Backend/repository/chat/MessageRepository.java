package me.huynhducphu.PingMe_Backend.repository.chat;

import me.huynhducphu.PingMe_Backend.model.chat.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Admin 8/25/2025
 *
 **/
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByRoom_IdAndSender_IdAndClientMsgId(Long roomId, Long senderId, UUID clientMsgId);

    @Query("""
            select m from Message m
            where m.room.id = :roomId
              and (:beforeId is null or m.id < :beforeId)
            order by m.id desc
            """)
    List<Message> findHistoryMessagesByKeySet(@Param("roomId") Long roomId,
                                              @Param("beforeId") Long beforeId,
                                              Pageable pageable);
}
