package me.huynhducphu.PingMe_Backend.dto.response.expense;

import lombok.Data;

@Data
public class DailyBreakdownResponse {

    private Integer day;
    private Double total;
}
