package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.ExpenseTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseTargetRepository extends JpaRepository<ExpenseTarget, Long> {

    Optional<ExpenseTarget> findByUserIdAndMonthAndYear(Long userId, int month, int year);
    void deleteByUserIdAndMonthAndYear(Long userId, int month, int year);
}
