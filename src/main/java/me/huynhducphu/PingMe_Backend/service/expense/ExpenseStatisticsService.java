package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.*;

import java.util.List;

public interface ExpenseStatisticsService {
    MonthStatisticsResponse getMonthStatistics(int month, int year);

    CategoryStatisticsResponse getCategoryStatistics(int month, int year);

    MonthComparisonResponse getMonthComparison(int month, int year);

    List<DailyBreakdownResponse> getDailyBreakdown(int month, int year);

    List<TopCategoryResponse> getTopCategories(int month, int year);

    YearStatisticsResponse getYearStatistics(int year);

    RangeStatisticsResponse getRangeStatistics(String from, String to);
}
