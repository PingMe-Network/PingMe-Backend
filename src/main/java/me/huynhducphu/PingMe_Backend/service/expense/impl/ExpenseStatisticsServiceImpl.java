package me.huynhducphu.PingMe_Backend.service.expense.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.miniapp.expense.*;
import me.huynhducphu.PingMe_Backend.model.miniapp.ExpenseTransaction;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;
import me.huynhducphu.PingMe_Backend.model.constant.TrendStatus;
import me.huynhducphu.PingMe_Backend.repository.ExpenseTransactionRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseStatisticsServiceImpl implements me.huynhducphu.PingMe_Backend.service.expense.ExpenseStatisticsService {

    private final ExpenseTransactionRepository txRepo;
    private final CurrentUserProvider currentUserProvider;

    private LocalDate startOfMonth(int month, int year) {
        return LocalDate.of(year, month, 1);
    }

    private LocalDate endOfMonth(int month, int year) {
        return LocalDate.of(year, month, 1)
                .plusMonths(1)
                .minusDays(1);
    }

    private List<ExpenseTransaction> getTransactions(int month, int year) {
        Long userId = currentUserProvider.get().getId();
        return txRepo.findByUserIdAndDateBetween(
                userId,
                startOfMonth(month, year),
                endOfMonth(month, year)
        );
    }

    @Override
    public MonthStatisticsResponse getMonthStatistics(int month, int year) {

        List<ExpenseTransaction> list = getTransactions(month, year);

        double income = list.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        double expense = list.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        MonthStatisticsResponse res = new MonthStatisticsResponse();
        res.setIncome(income);
        res.setExpense(expense);
        res.setNet(income - expense);

        return res;
    }

    @Override
    public CategoryStatisticsResponse getCategoryStatistics(int month, int year) {

        List<ExpenseTransaction> list = getTransactions(month, year);

        Map<String, Double> grouped = list.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().name(),
                        Collectors.summingDouble(ExpenseTransaction::getAmount)
                ));

        CategoryStatisticsResponse res = new CategoryStatisticsResponse();
        res.setTotalByCategory(grouped);
        return res;
    }

    @Override
    public MonthComparisonResponse getMonthComparison(int month, int year) {

        Long userId = currentUserProvider.get().getId();

        LocalDate startCurrent = startOfMonth(month, year);
        LocalDate endCurrent = endOfMonth(month, year);

        double expenseCurrent = txRepo.findByUserIdAndDateBetween(
                        userId, startCurrent, endCurrent
                ).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        LocalDate prevStart = LocalDate.of(year, month, 1).minusMonths(1);
        LocalDate prevEnd = prevStart.plusMonths(1).minusDays(1);

        double expensePrevious = txRepo.findByUserIdAndDateBetween(
                        userId, prevStart, prevEnd
                ).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        double diff = expenseCurrent - expensePrevious;
        double percent = expensePrevious == 0 ? 100 : (diff / expensePrevious) * 100;

        TrendStatus trend = diff > 0
                ? TrendStatus.UP
                : (diff < 0 ? TrendStatus.DOWN : TrendStatus.SAME);

        double incomeCurrent = txRepo.findByUserIdAndDateBetween(
                        userId, startCurrent, endCurrent
                ).stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        double incomePrevious = txRepo.findByUserIdAndDateBetween(
                        userId, prevStart, prevEnd
                ).stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        MonthComparisonResponse res = new MonthComparisonResponse();
        res.setCurrentMonth(expenseCurrent);
        res.setPreviousMonth(expensePrevious);
        res.setDiff(diff);
        res.setPercent(percent);
        res.setTrend(trend);

        res.setIncomeCurrentMonth(incomeCurrent);
        res.setIncomePreviousMonth(incomePrevious);

        return res;
    }

    @Override
    public List<DailyBreakdownResponse> getDailyBreakdown(int month, int year) {

        List<ExpenseTransaction> list = getTransactions(month, year);

        Map<Integer, Double> map = list.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getDate().getDayOfMonth(),
                        Collectors.summingDouble(ExpenseTransaction::getAmount)
                ));

        List<DailyBreakdownResponse> response = new ArrayList<>();

        int days = LocalDate.of(year, month, 1).lengthOfMonth();

        for (int d = 1; d <= days; d++) {
            DailyBreakdownResponse item = new DailyBreakdownResponse();
            item.setDay(d);
            item.setTotal(map.getOrDefault(d, 0.0));
            response.add(item);
        }

        return response;
    }

    @Override
    public List<TopCategoryResponse> getTopCategories(int month, int year) {

        List<ExpenseTransaction> list = getTransactions(month, year);

        Map<String, Double> grouped = list.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().name(),
                        Collectors.summingDouble(ExpenseTransaction::getAmount)
                ));

        return grouped.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(e -> {
                    TopCategoryResponse r = new TopCategoryResponse();
                    r.setCategory(e.getKey());
                    r.setTotal(e.getValue());
                    return r;
                })
                .toList();
    }

    @Override
    public YearStatisticsResponse getYearStatistics(int year) {

        Long userId = currentUserProvider.get().getId();

        double totalIncome = 0;
        double totalExpense = 0;

        List<YearStatisticsItemResponse> items = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            LocalDate start = LocalDate.of(year, m, 1);
            LocalDate end = start.plusMonths(1).minusDays(1);

            List<ExpenseTransaction> monthTx =
                    txRepo.findByUserIdAndDateBetween(userId, start, end);

            double income = monthTx.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .mapToDouble(ExpenseTransaction::getAmount)
                    .sum();

            double expense = monthTx.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .mapToDouble(ExpenseTransaction::getAmount)
                    .sum();

            YearStatisticsItemResponse item = new YearStatisticsItemResponse();
            item.setMonth(m);
            item.setIncome(income);
            item.setExpense(expense);
            item.setNet(income - expense);

            items.add(item);

            totalIncome += income;
            totalExpense += expense;
        }

        YearStatisticsResponse res = new YearStatisticsResponse();
        res.setYear(year);
        res.setItems(items);
        res.setTotalIncome(totalIncome);
        res.setTotalExpense(totalExpense);
        res.setTotalNet(totalIncome - totalExpense);

        return res;
    }

    @Override
    public RangeStatisticsResponse getRangeStatistics(String from, String to) {

        Long userId = currentUserProvider.get().getId();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate fromDate = LocalDate.parse(from, fmt);
        LocalDate toDate = LocalDate.parse(to, fmt);

        List<ExpenseTransaction> list =
                txRepo.findByUserIdAndDateBetween(userId, fromDate, toDate);

        double income = list.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        double expense = list.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(ExpenseTransaction::getAmount)
                .sum();

        Map<String, Double> byCategory = list.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().name(),
                        Collectors.summingDouble(ExpenseTransaction::getAmount)
                ));

        RangeStatisticsResponse res = new RangeStatisticsResponse();
        res.setFrom(from);
        res.setTo(to);
        res.setIncome(income);
        res.setExpense(expense);
        res.setNet(income - expense);
        res.setTotalByCategory(byCategory);

        return res;
    }
}
