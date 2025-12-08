package me.huynhducphu.PingMe_Backend.dto.response.expense;

import lombok.Data;

@Data
public class MonthStatisticsResponse {

    private Double income;
    private Double expense;
    private Double net;
}
