package me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense;

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
    private Double amount;
    private TransactionType type;
    private CategoryType category;
    private String note;
    private LocalDate date;
}