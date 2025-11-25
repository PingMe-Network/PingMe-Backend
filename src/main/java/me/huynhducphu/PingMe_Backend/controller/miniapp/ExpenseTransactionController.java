package me.huynhducphu.PingMe_Backend.controller.miniapp;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTransactionRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.DeleteTransactionResponse;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.TransactionResponse;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Expense Transactions",
        description = "Các API xử lý giao dịch thu chi"
)
@RestController
@RequestMapping("/expense/transactions")
@RequiredArgsConstructor
public class ExpenseTransactionController {

    private final ExpenseTransactionService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @RequestBody @Valid CreateTransactionRequest req
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.createTransaction(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> month(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.getTransactionsInMonth(month, year)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteTransactionResponse>> delete(@PathVariable Long id) {
        Long deletedId = service.deleteTransaction(id);
        return ResponseEntity.ok(
                new ApiResponse<>(new DeleteTransactionResponse(deletedId))
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(service.getTransactionDetail(id))
        );
    }
}
