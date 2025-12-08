package me.huynhducphu.PingMe_Backend.dto.response.expense;

import lombok.Data;
import me.huynhducphu.PingMe_Backend.model.constant.CategoryType;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;

import java.time.LocalDate;

@Data
public class TransactionResponse {
    private Long id;
    private Double amount;
    private String note;
    private LocalDate date;
    private TransactionType type;
    private CategoryType category;
}