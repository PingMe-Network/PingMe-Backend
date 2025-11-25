package me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.CategoryType;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTransactionRequest {
    @Positive(message = "Số tiền phải > 0")
    @NotNull
    private Double amount;
    @NotNull
    private TransactionType type;
    @NotNull
    private CategoryType category;
    @NotNull
    private String note;
    @NotNull
    private LocalDate date;
}