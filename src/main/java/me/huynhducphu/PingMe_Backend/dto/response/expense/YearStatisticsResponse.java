package me.huynhducphu.PingMe_Backend.dto.response.expense;

import lombok.Data;

import java.util.List;

@Data
public class YearStatisticsResponse {
    private Integer year;
    private List<YearStatisticsItemResponse> items;
    private Double totalIncome;
    private Double totalExpense;
    private Double totalNet;
}