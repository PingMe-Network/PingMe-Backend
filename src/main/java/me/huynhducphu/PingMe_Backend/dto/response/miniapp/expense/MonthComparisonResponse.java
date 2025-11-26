package me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense;

import lombok.Data;
import me.huynhducphu.PingMe_Backend.model.constant.TrendStatus;

@Data
public class MonthComparisonResponse {

    private Double currentMonth;
    private Double previousMonth;
    private Double diff;
    private Double percent;
    private TrendStatus trend;
    private Double incomeCurrentMonth;
    private Double incomePreviousMonth;
}
