package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTransactionRequest;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.TransactionResponse;

import java.util.List;

public interface ExpenseTransactionService {
    TransactionResponse createTransaction(CreateTransactionRequest request);

    List<TransactionResponse> getTransactionsInMonth(int month, int year);
}
