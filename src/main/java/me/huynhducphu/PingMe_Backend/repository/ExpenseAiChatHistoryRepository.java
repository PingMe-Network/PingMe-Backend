package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseAiChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ExpenseAiChatHistoryRepository extends JpaRepository<ExpenseAiChatHistory, Long> {
    List<ExpenseAiChatHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    Page<ExpenseAiChatHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
