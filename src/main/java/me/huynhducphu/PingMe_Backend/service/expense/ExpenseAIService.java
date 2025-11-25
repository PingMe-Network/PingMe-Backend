package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.AiChatRequest;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.AiChatHistoryResponse;
import org.springframework.data.domain.Page;

public interface ExpenseAIService {
    String ask(AiChatRequest request);

    Page<AiChatHistoryResponse> getHistory(int page, int size);
}
