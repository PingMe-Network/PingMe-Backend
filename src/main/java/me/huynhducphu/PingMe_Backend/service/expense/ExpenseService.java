package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTransactionRequest;
import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseTransaction;

public interface ExpenseService {
    ExpenseTransaction createTransaction(CreateTransactionRequest request);
}
