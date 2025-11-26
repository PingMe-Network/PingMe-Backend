package me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense;

import lombok.Data;

@Data
public class YearStatisticsItemResponse {
    private Integer month;
    private Double income;
    private Double expense;
    private Double net;
}