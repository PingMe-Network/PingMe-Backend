package me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import me.huynhducphu.PingMe_Backend.model.constant.BudgetStatus;

@Data
public class TargetResponse {
    private Integer month;
    private Integer year;
    private Double targetAmount;
    private Double spent;
    private Double percent;
    private BudgetStatus status;
}
