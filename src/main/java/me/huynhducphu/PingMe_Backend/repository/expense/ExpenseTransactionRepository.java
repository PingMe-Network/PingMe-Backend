package me.huynhducphu.PingMe_Backend.repository.expense;

import me.huynhducphu.PingMe_Backend.model.expense.ExpenseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {
    List<ExpenseTransaction> findByUserIdAndDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );
}