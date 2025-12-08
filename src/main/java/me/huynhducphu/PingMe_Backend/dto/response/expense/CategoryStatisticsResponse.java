package me.huynhducphu.PingMe_Backend.dto.response.expense;

import lombok.Data;

import java.util.Map;

@Data
public class CategoryStatisticsResponse {
    private Map<String, Double> totalByCategory;
}
