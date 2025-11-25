package me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense;

import lombok.Data;
import java.util.Map;

@Data
public class RangeStatisticsResponse {

    private String from;
    private String to;

    private Double income;
    private Double expense;
    private Double net;

    private Map<String, Double> totalByCategory;
}
