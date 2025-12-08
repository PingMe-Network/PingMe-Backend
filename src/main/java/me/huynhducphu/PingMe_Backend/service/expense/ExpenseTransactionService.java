package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTransactionRequest;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.UpdateTransactionRequest;
import me.huynhducphu.PingMe_Backend.dto.response.expense.TransactionResponse;

import java.util.List;

public interface ExpenseTransactionService {
    TransactionResponse createTransaction(CreateTransactionRequest request);

    List<TransactionResponse> getTransactionsInMonth(int month, int year);

    Long deleteTransaction(Long id);

    TransactionResponse getTransactionDetail(Long id);

    TransactionResponse updateTransaction(Long id, UpdateTransactionRequest request);
}
