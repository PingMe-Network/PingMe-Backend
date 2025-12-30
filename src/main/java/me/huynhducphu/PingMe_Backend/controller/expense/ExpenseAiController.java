package me.huynhducphu.PingMe_Backend.controller.expense;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.AiChatRequest;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.expense.AiChatHistoryResponse;
import me.huynhducphu.PingMe_Backend.dto.response.expense.AiChatResponse;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseAIService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense/ai")
@RequiredArgsConstructor
public class ExpenseAiController {

    private final ExpenseAIService expenseAIService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(
            @RequestBody @Valid AiChatRequest request
    ) {
        String answer = expenseAIService.ask(request);
        return ResponseEntity.ok(new ApiResponse<>(new AiChatResponse(answer)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<AiChatHistoryResponse>>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(expenseAIService.getHistory(page, size))
        );
    }
}
