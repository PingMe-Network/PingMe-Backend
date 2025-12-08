package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTargetRequest;
import me.huynhducphu.PingMe_Backend.dto.response.expense.TargetResponse;

public interface ExpenseTargetService {
    TargetResponse setTarget(CreateTargetRequest request);

    TargetResponse getTarget(int month, int year);

    void deleteTarget(int month, int year);
}
