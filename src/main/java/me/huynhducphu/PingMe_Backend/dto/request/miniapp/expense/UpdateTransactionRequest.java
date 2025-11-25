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
    @NotNull(message = "Số tiền không được để trống")
    private Double amount;
    @NotNull(message = "Loại giao dịch không được để trống")
    private TransactionType type;
    @NotNull(message = "Danh mục không được để trống")
    private CategoryType category;
    @NotNull(message = "Ghi chú không được để trống")
    private String note;
    @NotNull(message = "Ngày giao dịch không được để trống")
    private LocalDate date;
}