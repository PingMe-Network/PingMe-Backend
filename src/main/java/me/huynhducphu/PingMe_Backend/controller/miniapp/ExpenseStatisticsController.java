package me.huynhducphu.PingMe_Backend.controller.miniapp;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.CreateTargetRequest;
import me.huynhducphu.PingMe_Backend.dto.response.common.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.*;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseStatisticsService;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseTargetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Expense Statistics",
        description = "Các API thống kê chi tiêu, mục tiêu chi tiêu"
)
@RestController
@RequestMapping("/expense/statistics")
@RequiredArgsConstructor
public class ExpenseStatisticsController {

    private final ExpenseStatisticsService service;
    private final ExpenseTargetService targetService;

    @GetMapping("/month")
    public ResponseEntity<ApiResponse<MonthStatisticsResponse>> month(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.getMonthStatistics(month, year)));
    }

    @GetMapping("/target")
    public ResponseEntity<ApiResponse<TargetResponse>> getTarget(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(targetService.getTarget(month, year))
        );
    }

    @PostMapping("/target")
    public ResponseEntity<ApiResponse<TargetResponse>> setTarget(
            @RequestBody @Valid CreateTargetRequest request
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(targetService.setTarget(request))
        );
    }

    @GetMapping("/by-category")
    public ResponseEntity<ApiResponse<CategoryStatisticsResponse>> category(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.getCategoryStatistics(month, year)));
    }

    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<MonthComparisonResponse>> compare(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.getMonthComparison(month, year)));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailyBreakdownResponse>>> daily(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.getDailyBreakdown(month, year)));
    }

    @GetMapping("/top-categories")
    public ResponseEntity<ApiResponse<List<TopCategoryResponse>>> topCategories(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return ResponseEntity.ok(new ApiResponse<>(service.getTopCategories(month, year)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteTarget(
            @RequestParam int month,
            @RequestParam int year
    ) {
        targetService.deleteTarget(month, year);
        return ResponseEntity.ok(new ApiResponse<>((Void) null));
    }

    @GetMapping("/year")
    public ResponseEntity<ApiResponse<YearStatisticsResponse>> year(
            @RequestParam int year
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(service.getYearStatistics(year))
        );
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<RangeStatisticsResponse>> range(
            @RequestParam String from,
            @RequestParam String to
    ) {
        return ResponseEntity.ok(
                new ApiResponse<>(service.getRangeStatistics(from, to))
        );
    }
}
