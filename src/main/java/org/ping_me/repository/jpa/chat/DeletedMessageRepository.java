package org.ping_me.repository.jpa.chat;

import org.ping_me.model.chat.DeletedMessage;
import org.ping_me.model.common.DeletedMessageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeletedMessageRepository extends JpaRepository<DeletedMessage, DeletedMessageId> {
    boolean existsByIdRoomIdAndIdUserId(Long roomId, Long userId);

    List<DeletedMessage> findByIdRoomIdAndIdUserId(Long roomId, Long userId);

    long deleteByIdRoomId(Long roomId);
}
