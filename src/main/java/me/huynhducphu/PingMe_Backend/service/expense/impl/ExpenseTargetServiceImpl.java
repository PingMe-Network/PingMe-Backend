package me.huynhducphu.PingMe_Backend.service.expense.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTargetRequest;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.TargetResponse;
import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseTarget;
import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseTransaction;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.constant.BudgetStatus;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;
import me.huynhducphu.PingMe_Backend.repository.ExpenseTargetRepository;
import me.huynhducphu.PingMe_Backend.repository.ExpenseTransactionRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseTargetServiceImpl implements me.huynhducphu.PingMe_Backend.service.expense.ExpenseTargetService {

    private final ExpenseTargetRepository targetRepo;
    private final ExpenseTransactionRepository txRepo;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public TargetResponse setTarget(CreateTargetRequest request) {
        User user = currentUserProvider.get();

        ExpenseTarget target = targetRepo
                .findByUserIdAndMonthAndYear(user.getId(), request.getMonth(), request.getYear())
                .orElse(new ExpenseTarget());

        target.setUser(user);
        target.setMonth(request.getMonth());
        target.setYear(request.getYear());
        target.setTargetAmount(request.getTargetAmount());

        targetRepo.save(target);

        return getTarget(request.getMonth(), request.getYear());
    }

    @Override
    public TargetResponse getTarget(int month, int year) {
        User user = currentUserProvider.get();

        ExpenseTarget target = targetRepo
                .findByUserIdAndMonthAndYear(user.getId(), month, year)
                .orElse(null);

        double spent = calculateSpent(user.getId(), month, year);

        TargetResponse res = new TargetResponse();
        res.setMonth(month);
        res.setYear(year);
        res.setSpent(spent);

        if (target != null) {
            res.setTargetAmount(target.getTargetAmount());
            res.setPercent((spent / target.getTargetAmount()) * 100);
            res.setStatus(
                    spent <= target.getTargetAmount()
                            ? BudgetStatus.ON_TRACK
                            : BudgetStatus.OVER_LIMIT
            );

        }

        return res;
    }

    private double calculateSpent(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        return txRepo.findByUserIdAndDateBetween(userId, start, end)
                .stream()
                .filter(tx -> tx.getType() == TransactionType.EXPENSE)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();
    }

    @Override
    @Transactional
    public void deleteTarget(int month, int year) {
        User user = currentUserProvider.get();
        targetRepo.deleteByUserIdAndMonthAndYear(user.getId(), month, year);
    }
}
