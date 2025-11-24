package me.huynhducphu.PingMe_Backend.service.expense.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.*;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseStatisticsService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpenseStatisticsToolsServiceImpl implements me.huynhducphu.PingMe_Backend.service.expense.ExpenseStatisticsToolsService {

    private final ExpenseStatisticsService statisticsService;

    private int currentMonth() { return LocalDate.now().getMonthValue(); }
    private int currentYear()  { return LocalDate.now().getYear(); }

    @Tool(description = """
            Lấy thống kê tháng: tổng thu, tổng chi, net.
            Dùng khi user hỏi: "tháng này thu chi bao nhiêu", "tổng quan tháng 11", "net tháng này".
            month/year null => mặc định hiện tại.
            """)
    @Override
    public MonthStatisticsResponse getMonthStatistics(Integer month, Integer year) {
        int m = (month == null) ? currentMonth() : month;
        int y = (year == null) ? currentYear() : year;
        return statisticsService.getMonthStatistics(m, y);
    }

    @Tool(description = """
            Thống kê chi tiêu theo danh mục trong tháng.
            Dùng khi user hỏi: "chi theo danh mục tháng này", "ăn uống bao nhiêu tháng 11".
            """)
    @Override
    public CategoryStatisticsResponse getCategoryStatistics(Integer month, Integer year) {
        int m = (month == null) ? currentMonth() : month;
        int y = (year == null) ? currentYear() : year;
        return statisticsService.getCategoryStatistics(m, y);
    }

    @Tool(description = """
            So sánh chi tiêu tháng hiện tại với tháng trước.
            Dùng khi user hỏi: "tháng này chi hơn tháng trước không", "xu hướng chi tiêu".
            """)
    @Override
    public MonthComparisonResponse getMonthComparison(Integer month, Integer year) {
        int m = (month == null) ? currentMonth() : month;
        int y = (year == null) ? currentYear() : year;
        return statisticsService.getMonthComparison(m, y);
    }

    @Tool(description = """
            Breakdown chi tiêu theo từng ngày trong tháng.
            Dùng khi user hỏi: "chi theo ngày tháng này", "mỗi ngày tiêu bao nhiêu".
            """)
    @Override
    public List<DailyBreakdownResponse> getDailyBreakdown(Integer month, Integer year) {
        int m = (month == null) ? currentMonth() : month;
        int y = (year == null) ? currentYear() : year;
        return statisticsService.getDailyBreakdown(m, y);
    }

    @Tool(description = """
            Top danh mục chi tiêu trong tháng (giảm dần).
            Dùng khi user hỏi: "top danh mục tháng này", "chi nhiều nhất vào đâu".
            """)
    @Override
    public List<TopCategoryResponse> getTopCategories(Integer month, Integer year) {
        int m = (month == null) ? currentMonth() : month;
        int y = (year == null) ? currentYear() : year;
        return statisticsService.getTopCategories(m, y);
    }

    @Tool(description = """
            Thống kê theo năm: thu/chi từng tháng và tổng cả năm.
            Dùng khi user hỏi: "thống kê năm nay", "báo cáo năm 2025".
            year null => mặc định năm hiện tại.
            """)
    @Override
    public YearStatisticsResponse getYearStatistics(Integer year) {
        int y = (year == null) ? currentYear() : year;
        return statisticsService.getYearStatistics(y);
    }

    @Tool(description = """
            Thống kê theo khoảng ngày from->to (yyyy-MM-dd).
            Dùng khi user hỏi: "thống kê từ 1/11 đến 10/11", "tuần này tôi tiêu bao nhiêu".
            """)
    @Override
    public RangeStatisticsResponse getRangeStatistics(String from, String to) {
        return statisticsService.getRangeStatistics(from, to);
    }
}
