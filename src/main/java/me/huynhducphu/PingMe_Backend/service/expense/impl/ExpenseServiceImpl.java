package me.huynhducphu.PingMe_Backend.service.expense.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTransactionRequest;
import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseTransaction;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.repository.ExpenseTransactionRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseTransactionRepository transactionRepo;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ExpenseTransaction createTransaction(CreateTransactionRequest request) {

        User user = currentUserProvider.get();

        ExpenseTransaction tx = new ExpenseTransaction();
        tx.setAmount(request.getAmount());
        tx.setNote(request.getNote());
        tx.setDate(request.getDate());
        tx.setType(request.getType());
        tx.setCategory(request.getCategory());
        tx.setUser(user);

        return transactionRepo.save(tx);
    }
}
