package me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.huynhducphu.PingMe_Backend.model.constant.CategoryType;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateTransactionRequest {

    @NotNull(message = "Số tiền không được để trống")
    private Double amount;

    @NotNull(message = "Loại giao dịch không được để trống")
    private TransactionType type;

    @NotNull(message = "Danh mục không được để trống")
    private CategoryType category;

    private String note;

    @NotNull(message = "Ngày không được để trống")
    private LocalDate date;
}