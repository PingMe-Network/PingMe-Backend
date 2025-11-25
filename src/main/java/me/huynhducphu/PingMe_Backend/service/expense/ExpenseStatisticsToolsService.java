package me.huynhducphu.PingMe_Backend.service.expense;

import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.*;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

public interface ExpenseStatisticsToolsService {
    @Tool(description = """
            Lấy thống kê tháng: tổng thu, tổng chi, net.
            Dùng khi user hỏi: "tháng này thu chi bao nhiêu", "tổng quan tháng 11", "net tháng này".
            month/year null => mặc định hiện tại.
            """)
    MonthStatisticsResponse getMonthStatistics(Integer month, Integer year);

    @Tool(description = """
            Thống kê chi tiêu theo danh mục trong tháng.
            Dùng khi user hỏi: "chi theo danh mục tháng này", "ăn uống bao nhiêu tháng 11".
            """)
    CategoryStatisticsResponse getCategoryStatistics(Integer month, Integer year);

    @Tool(description = """
            So sánh chi tiêu tháng hiện tại với tháng trước.
            Dùng khi user hỏi: "tháng này chi hơn tháng trước không", "xu hướng chi tiêu".
            """)
    MonthComparisonResponse getMonthComparison(Integer month, Integer year);

    @Tool(description = """
            Breakdown chi tiêu theo từng ngày trong tháng.
            Dùng khi user hỏi: "chi theo ngày tháng này", "mỗi ngày tiêu bao nhiêu".
            """)
    List<DailyBreakdownResponse> getDailyBreakdown(Integer month, Integer year);

    @Tool(description = """
            Top danh mục chi tiêu trong tháng (giảm dần).
            Dùng khi user hỏi: "top danh mục tháng này", "chi nhiều nhất vào đâu".
            """)
    List<TopCategoryResponse> getTopCategories(Integer month, Integer year);

    @Tool(description = """
            Thống kê theo năm: thu/chi từng tháng và tổng cả năm.
            Dùng khi user hỏi: "thống kê năm nay", "báo cáo năm 2025".
            year null => mặc định năm hiện tại.
            """)
    YearStatisticsResponse getYearStatistics(Integer year);

    @Tool(description = """
            Thống kê theo khoảng ngày from->to (yyyy-MM-dd).
            Dùng khi user hỏi: "thống kê từ 1/11 đến 10/11", "tuần này tôi tiêu bao nhiêu".
            """)
    RangeStatisticsResponse getRangeStatistics(String from, String to);
}
