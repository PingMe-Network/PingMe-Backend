package me.huynhducphu.PingMe_Backend.dto.response.expense;

import lombok.Data;

@Data
public class TopCategoryResponse {
    private String category;
    private Double total;
}
