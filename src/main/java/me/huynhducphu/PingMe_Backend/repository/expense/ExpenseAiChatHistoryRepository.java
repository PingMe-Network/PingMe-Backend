package me.huynhducphu.PingMe_Backend.repository.expense;

import me.huynhducphu.PingMe_Backend.model.expense.ExpenseAiChatHistory;
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
