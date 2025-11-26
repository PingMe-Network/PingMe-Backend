package me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTargetRequest {

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;

    @NotNull
    private Double targetAmount;
}
