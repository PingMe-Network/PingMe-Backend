package me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense;

import lombok.Data;
import me.huynhducphu.PingMe_Backend.model.constant.CategoryType;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Double amount;
    private String note;
    private LocalDate date;
    private TransactionType type;
    private CategoryType category;
}