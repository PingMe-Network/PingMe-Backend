package me.huynhducphu.PingMe_Backend.service.expense.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.miniapp.expense.AiChatRequest;
import me.huynhducphu.PingMe_Backend.dto.response.expense.AiChatHistoryResponse;
import me.huynhducphu.PingMe_Backend.model.expense.ExpenseAiChatHistory;
import me.huynhducphu.PingMe_Backend.model.expense.ExpenseTransaction;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.constant.AiChatRole;
import me.huynhducphu.PingMe_Backend.model.constant.CategoryType;
import me.huynhducphu.PingMe_Backend.model.constant.TransactionType;
import me.huynhducphu.PingMe_Backend.repository.expense.ExpenseAiChatHistoryRepository;
import me.huynhducphu.PingMe_Backend.repository.expense.ExpenseTransactionRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseAIService;
import me.huynhducphu.PingMe_Backend.service.expense.ExpenseStatisticsToolsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseAIServiceImpl implements ExpenseAIService {

    private final ChatClient chatClient;
    private final ExpenseTools expenseTools;
    private final ExpenseAiChatHistoryRepository historyRepo;
    private final CurrentUserProvider currentUserProvider;
    private final ExpenseStatisticsToolsService statisticsTools;

    @Override
    public String ask(AiChatRequest request) {

        User user = currentUserProvider.get();

//        String inferredDate = inferRelativeDate(request.getPrompt());
        String inferredDate = inferDate(request.getPrompt());

        List<ExpenseAiChatHistory> lastHistories =
                historyRepo.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());

        List<ExpenseAiChatHistory> orderedHistories = lastHistories.stream()
                .sorted(Comparator.comparing(ExpenseAiChatHistory::getCreatedAt))
                .toList();

        String historyContext = orderedHistories.stream()
                .filter(h -> {
                    if (h.getRole() == AiChatRole.AI) {
                        String c = h.getContent().toLowerCase(Locale.ROOT);
                        return !c.contains("chắc chắn muốn xóa");
                    }
                    return true;
                })
                .map(h -> switch (h.getRole()) {
                    case USER -> "User: " + h.getContent();
                    case AI -> "AI: " + h.getContent();
                })
                .collect(Collectors.joining("\n"));

        String prompt = """
                Bạn là trợ lý AI quản lý chi tiêu cá nhân cho ứng dụng PingMe.
                
                LỊCH SỬ TRAO ĐỔI GẦN ĐÂY (để hiểu ngữ cảnh):
                %s
                
                ---
                Bạn có các công cụ:
                
                CRUD giao dịch:
                1) addTransaction(amount, category, type, note, date)
                2) deleteTransaction(amount, category, note, date)
                3) updateTransaction(oldAmount, oldCategory, oldNote, oldDate, newAmount, newCategory, newNote, newDate)
                
                Thống kê tay (backend cung cấp):
                4) getMonthStatistics(month, year)
                5) getCategoryStatistics(month, year)
                6) getMonthComparison(month, year)
                7) getDailyBreakdown(month, year)
                8) getTopCategories(month, year)
                9) getYearStatistics(year)
                10) getRangeStatistics(from, to)
                
                QUY TẮC:
                - Nếu user hỏi thống kê/báo cáo/tổng thu-chi/xu hướng/top danh mục
                  => PHẢI gọi tool thống kê tương ứng trước rồi mới trả lời.
                - Không được nói "tôi không thể thống kê".
                
                HƯỚNG DẪN DÙNG TOOL:
                - Khi người dùng muốn thêm chi tiêu/thu nhập (vd: "tôi tiêu 200k ăn tối", "lương tháng 11 8tr"),
                  hãy gọi addTransaction.
                - Khi người dùng muốn xóa giao dịch:
                    + Nếu KHÔNG nói số tiền (amount) và KHÔNG nói "xóa hết/xóa tất cả/không nhớ số tiền"
                      => HỎI LẠI số tiền để xóa chính xác.
                    + Nếu có nói "xóa hết/xóa tất cả/không nhớ số tiền"
                      => gọi deleteTransaction với amount = null để xóa TẤT CẢ giao dịch phù hợp.
                - Khi muốn sửa (vd: "sửa ăn tối 50k thành 70k"), hãy gọi updateTransaction.
                - Nếu người dùng chỉ hỏi tư vấn/thống kê, KHÔNG gọi tool, chỉ trả lời.
                
                QUY TẮC QUAN TRỌNG:
                - Kể cả khi người dùng lặp lại đúng câu lệnh thêm/sửa/xóa trước đó, hãy coi đó là yêu cầu MỚI và vẫn phải gọi tool tương ứng (trừ khi họ nói rõ ‘không cần thêm nữa
                - Tuyệt đối KHÔNG tự tạo ngày yyyy-MM-dd nếu người dùng không nói rõ ngày cụ thể.
                - Nếu người dùng nói ngày tương đối như "hôm nay/tối nay/sáng nay/chiều nay/hôm qua" hoặc không nói ngày
                  => date đã được backend suy ra bên dưới và bạn PHẢI dùng đúng date đó khi gọi tool, KHÔNG hỏi lại.
                - Chỉ hỏi lại ngày khi người dùng nói mơ hồ kiểu "hôm trước/bữa kia" mà không rõ thời điểm.
                - Nếu thiếu amount hoặc category quan trọng thì hỏi lại, không gọi tool sai.
                - Không bịa dữ liệu.
                - Trả lời ngắn gọn, rõ ràng tiếng Việt.
                        QUY TẮC XÓA (BẮT BUỘC LÀM THEO):
                        - inferredDate là ngày chắc chắn do backend suy ra.
                        - Nếu inferredDate khác null và người dùng yêu cầu xóa nhưng KHÔNG nói số tiền
                          => PHẢI gọi deleteTransaction(amount=null, category=..., date=inferredDate) để xóa TẤT CẢ trong ngày đó.
                          => TUYỆT ĐỐI KHÔNG hỏi lại, KHÔNG xác nhận.
                        - Nếu inferredDate là null và người dùng không nói số tiền
                          => hỏi lại số tiền.
                        - Không được hỏi kiểu "Bạn có chắc chắn không?" trong mọi trường hợp xóa.
                
                Danh mục hợp lệ (enum):
                FOOD_AND_BEVERAGE (ăn uống, ăn tối, ăn sáng, nhà hàng...)
                COFFEE (cà phê, trà sữa...)
                TRANSPORTATION (đi lại, grab, taxi...)
                GAS (đổ xăng...)
                SHOPPING (mua sắm...)
                HOUSEHOLD (gia dụng, đồ nhà...)
                ELECTRICITY (tiền điện...)
                WATER (tiền nước...)
                INTERNET (wifi, mạng...)
                PHONE (điện thoại, sim, 4g/5g...)
                ENTERTAINMENT (giải trí...)
                HEALTHCARE (y tế, thuốc...)
                PETS (thú cưng...)
                GIFTS (quà tặng...)
                EDUCATION (học phí, sách vở...)
                TRAVEL (du lịch...)
                OTHER (khác...)
                
                Ngữ cảnh chắc chắn do backend suy ra:
                - inferredDate = %s
                  (Nếu inferredDate khác null => đó là ngày chính xác phải dùng khi gọi tool.)
                
                Người dùng: %s
                """.formatted(
                historyContext.isBlank() ? "(Chưa có lịch sử trước đó)" : historyContext,
                inferredDate,
                request.getPrompt()
        );

        saveHistory(user, AiChatRole.USER, request.getPrompt());
        String answer = chatClient
                .prompt(prompt)
                .tools(expenseTools, statisticsTools)
                .call()
                .content();
        saveHistory(user, AiChatRole.AI, answer);

        return answer;
    }

    private void saveHistory(User user, AiChatRole role, String content) {
        ExpenseAiChatHistory h = new ExpenseAiChatHistory();
        h.setUser(user);
        h.setRole(role);
        h.setContent(content);
        historyRepo.save(h);
    }

    private String inferRelativeDate(String userPrompt) {
        if (userPrompt == null) return null;
        String s = userPrompt.toLowerCase(Locale.ROOT);

        if (s.contains("hôm nay") || s.contains("tối nay")
                || s.contains("sáng nay") || s.contains("chiều nay")) {
            return LocalDate.now().toString();
        }
        if (s.contains("hôm qua")) {
            return LocalDate.now().minusDays(1).toString();
        }
        return null;
    }

    @Component
    @RequiredArgsConstructor
    public static class ExpenseTools {

        private final ExpenseTransactionRepository txRepo;
        private final CurrentUserProvider currentUserProvider;
        private final ExpenseStatisticsToolsService statisticsTools;

        @Tool(description = """
                Thêm một giao dịch thu/chi cho người dùng hiện tại.
                Dùng khi người dùng muốn ghi nhận chi tiêu/thu nhập mới.
                """)
        public String addTransaction(
                Double amount,
                String category,
                String type,
                String note,
                String date
        ) {
            User user = currentUserProvider.get();

            if (amount == null || amount <= 0) {
                return "Tôi chưa thấy số tiền hợp lệ. Bạn nhập lại giúp tôi.";
            }

            CategoryType cat = mapCategory(category);
            if (cat == null) {
                cat = CategoryType.OTHER;
            }

            TransactionType txType;
            try {
                txType = (type == null || type.isBlank())
                        ? TransactionType.EXPENSE
                        : TransactionType.valueOf(type.toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                txType = TransactionType.EXPENSE;
            }

            LocalDate txDate;
            try {
                txDate = (date == null || date.isBlank())
                        ? LocalDate.now()
                        : LocalDate.parse(date);
            } catch (Exception e) {
                txDate = LocalDate.now();
            }

            ExpenseTransaction tx = new ExpenseTransaction();
            tx.setAmount(amount);
            tx.setCategory(cat);
            tx.setType(txType);
            tx.setNote(note);
            tx.setDate(txDate);
            tx.setUser(user);

            txRepo.save(tx);

            return String.format(
                    "Đã thêm giao dịch %s %,.0f VND (%s) vào ngày %s.",
                    cat.name(), amount, txType.name(), txDate
            );
        }

        @Tool(description = """
                - Khi người dùng muốn xóa giao dịch:
                    + Nếu người dùng KHÔNG nói số tiền (amount):
                        * Nếu người dùng đã nói rõ ngày cụ thể (ví dụ "ngày 10 tháng 11", "9/11")
                          => gọi deleteTransaction với amount = null để XÓA TẤT CẢ giao dịch phù hợp trong ngày đó.
                        * Nếu người dùng KHÔNG nói rõ ngày cụ thể
                          => hỏi lại số tiền để xóa chính xác, trừ khi họ nói "xóa hết/xóa tất cả/không nhớ số tiền".
                    + Nếu người dùng nói "xóa hết/xóa tất cả/không nhớ số tiền"
                      => gọi deleteTransaction với amount = null để xóa tất cả giao dịch phù hợp.
                      \s""")
        public String deleteTransaction(
                Double amount,
                String category,
                String note,
                String date
        ) {
            User user = currentUserProvider.get();

            System.out.println("[AI deleteTransaction] amount=" + amount
                    + ", category=" + category
                    + ", note=" + note
                    + ", date=" + date);

            LocalDate end = LocalDate.now();
            LocalDate start = end.minusMonths(12);

            List<ExpenseTransaction> list =
                    txRepo.findByUserIdAndDateBetween(user.getId(), start, end)
                            .stream()
                            .sorted(Comparator.comparing(ExpenseTransaction::getDate).reversed())
                            .toList();

            if (list.isEmpty()) {
                return "Bạn chưa có giao dịch nào để xóa.";
            }

            final CategoryType cat =
                    (category != null && !category.isBlank())
                            ? Optional.ofNullable(mapCategory(category)).orElse(CategoryType.OTHER)
                            : null;

            LocalDate dTmp = null;
            if (date != null && !date.isBlank()) {
                try {
                    dTmp = LocalDate.parse(date);
                } catch (Exception ignored) {
                }
            }
            final LocalDate d = dTmp;

            final String noteKw =
                    (note != null && !note.isBlank()) ? normalize(note) : null;

            if (amount != null) {
                Optional<ExpenseTransaction> match =
                        findNearestMatch(user.getId(), amount, category, note, date);

                if (match.isEmpty()) {
                    return "Tôi không tìm thấy giao dịch phù hợp để xóa. Bạn nói rõ hơn giúp tôi.";
                }

                ExpenseTransaction tx = match.get();
                txRepo.deleteById(tx.getId());

                return String.format(
                        "Đã xóa giao dịch %s %,.0f VND (%s).",
                        tx.getCategory().name(), tx.getAmount(), tx.getDate()
                );
            }

            List<ExpenseTransaction> matches = list.stream()
                    .filter(tx -> matchTx(tx, null, cat, noteKw, d, false))
                    .toList();

            if (matches.isEmpty()) {
                return "Tôi không tìm thấy giao dịch phù hợp để xóa. Bạn nói rõ hơn giúp tôi.";
            }

            txRepo.deleteAll(matches);

            return String.format(
                    "Đã xóa %d giao dịch phù hợp%s%s.",
                    matches.size(),
                    (cat != null ? " trong danh mục " + cat.name() : ""),
                    (d != null ? " vào ngày " + d : "")
            );
        }

        @Tool(description = """
                Sửa giao dịch gần nhất phù hợp với thông tin cũ, thay bằng thông tin mới.
                Dùng khi người dùng muốn chỉnh sửa giao dịch.
                """)
        public String updateTransaction(
                Double oldAmount,
                String oldCategory,
                String oldNote,
                String oldDate,

                Double newAmount,
                String newCategory,
                String newNote,
                String newDate
        ) {
            User user = currentUserProvider.get();

            Optional<ExpenseTransaction> match =
                    findNearestMatch(user.getId(), oldAmount, oldCategory, oldNote, oldDate);

            if (match.isEmpty()) {
                return "Tôi không tìm thấy giao dịch phù hợp để chỉnh sửa. Bạn nói rõ hơn giúp tôi.";
            }

            ExpenseTransaction tx = match.get();

            if (newAmount != null && newAmount > 0) {
                tx.setAmount(newAmount);
            }

            if (newCategory != null && !newCategory.isBlank()) {
                CategoryType mapped = mapCategory(newCategory);
                if (mapped != null) tx.setCategory(mapped);
            }

            if (newNote != null && !newNote.isBlank()) {
                tx.setNote(newNote);
            }

            if (newDate != null && !newDate.isBlank()) {
                try {
                    tx.setDate(LocalDate.parse(newDate));
                } catch (Exception ignored) {
                }
            }

            txRepo.save(tx);

            return String.format(
                    "Đã chỉnh sửa giao dịch thành %s %,.0f VND (%s).",
                    tx.getCategory().name(), tx.getAmount(), tx.getDate()
            );
        }

        private Optional<ExpenseTransaction> findNearestMatch(
                Long userId, Double amount, String category, String note, String date
        ) {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusMonths(12);

            List<ExpenseTransaction> list =
                    txRepo.findByUserIdAndDateBetween(userId, start, end)
                            .stream()
                            .sorted(Comparator.comparing(ExpenseTransaction::getDate).reversed())
                            .toList();

            if (list.isEmpty()) return Optional.empty();

            final CategoryType cat =
                    (category != null && !category.isBlank()) ? mapCategory(category) : null;

            LocalDate dTmp = null;
            if (date != null && !date.isBlank()) {
                try {
                    dTmp = LocalDate.parse(date);
                } catch (Exception ignored) {
                }
            }
            final LocalDate d = dTmp;

            final String noteKw =
                    (note != null && !note.isBlank()) ? normalize(note) : null;

            Optional<ExpenseTransaction> fullMatch = list.stream()
                    .filter(tx -> matchTx(tx, amount, cat, noteKw, d, true))
                    .findFirst();
            if (fullMatch.isPresent()) return fullMatch;

            return list.stream()
                    .filter(tx -> matchTx(tx, amount, cat, noteKw, d, false))
                    .findFirst();
        }

        private boolean matchTx(
                ExpenseTransaction tx,
                Double amount,
                CategoryType cat,
                String noteKw,
                LocalDate d,
                boolean useNote
        ) {
            boolean ok = true;

            if (amount != null) {
                ok &= Math.abs(tx.getAmount() - amount) < 0.5;
            }
            if (cat != null) {
                ok &= tx.getCategory() == cat;
            }
            if (d != null) {
                ok &= tx.getDate().equals(d);
            }
            if (useNote && noteKw != null && tx.getNote() != null) {
                ok &= normalize(tx.getNote()).contains(noteKw);
            }

            return ok;
        }

        private CategoryType mapCategory(String raw) {
            if (raw == null) return null;
            String s = normalize(raw);

            if (containsAnyWord(s, "an uong", "an toi", "an sang", "an trua", "com", "bun", "pho", "lau",
                    "do an", "an vat", "mon an", "nha hang", "quan an", "food", "drink", "beverage"))
                return CategoryType.FOOD_AND_BEVERAGE;

            if (containsAnyWord(s, "coffee", "ca phe", "cafe", "tra sua", "milktea", "tra", "tea"))
                return CategoryType.COFFEE;

            if (containsAnyWord(s, "di lai", "xe bus", "bus", "metro", "taxi", "grab", "gojek", "be",
                    "xe om", "ship", "van chuyen", "transport", "tien xe"))
                return CategoryType.TRANSPORTATION;

            if (containsAnyWord(s, "xang", "do xang", "xang xe", "gas", "fuel", "dau"))
                return CategoryType.GAS;

            if (containsAnyWord(s, "mua sam", "shopping", "ao quan", "quan ao", "giay dep", "my pham",
                    "do dung", "mall", "sieu thi", "shopee", "lazada", "tiki"))
                return CategoryType.SHOPPING;

            if (containsAnyWord(s, "do gia dung", "gia dung", "noi that", "household", "do nha",
                    "sua nha", "ve sinh nha", "lau don"))
                return CategoryType.HOUSEHOLD;

            if (containsAnyWord(s, "tien dien", "dien", "electric", "electricity"))
                return CategoryType.ELECTRICITY;

            if (containsAnyWord(s, "tien nuoc", "nuoc", "water bill"))
                return CategoryType.WATER;

            if (containsAnyWord(s, "internet", "wifi", "cap quang", "mang"))
                return CategoryType.INTERNET;

            if (containsAnyWord(s, "dien thoai", "phone", "nap the", "cuoc goi", "4g", "5g", "sim"))
                return CategoryType.PHONE;

            if (containsAnyWord(s, "giai tri", "xem phim", "rap", "netflix", "spotify", "game", "karaoke",
                    "entertainment"))
                return CategoryType.ENTERTAINMENT;

            if (containsAnyWord(s, "y te", "kham benh", "thuoc", "benh vien", "nha thuoc",
                    "health", "healthcare", "doctor", "hospital"))
                return CategoryType.HEALTHCARE;

            if (containsAnyWord(s, "thu cung", "pet", "cho", "meo", "cat", "dog", "thuc an cho meo")) {
                return CategoryType.PETS;
            }

            if (containsAnyWord(s, "qua", "tang qua", "sinh nhat", "gift", "mung", "cuoi hoi"))
                return CategoryType.GIFTS;

            if (containsAnyWord(s, "hoc phi", "khoa hoc", "lop hoc", "sach", "hoc tap",
                    "education", "course", "school", "university"))
                return CategoryType.EDUCATION;

            if (containsAnyWord(s, "du lich", "travel", "khach san", "hotel", "ve may bay", "tour"))
                return CategoryType.TRAVEL;

            if (containsAnyWord(s, "khac", "other", "linh tinh"))
                return CategoryType.OTHER;

            return null;
        }

        private String normalize(String input) {
            String s = input.toLowerCase(Locale.ROOT).trim();

            s = s.replace("đ", "d");
            s = s.replace("ă", "a").replace("â", "a").replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a");
            s = s.replace("ê", "e").replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e");
            s = s.replace("ô", "o").replace("ơ", "o").replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o");
            s = s.replace("ư", "u").replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u");
            s = s.replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i");
            s = s.replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y");

            s = s.replaceAll("\\s+", " ");
            return s;
        }

        private boolean containsAny(String s, String... keys) {
            for (String k : keys) {
                if (s.contains(k)) return true;
            }
            return false;
        }
    }

    private static boolean containsAnyWord(String s, String... keys) {
        String[] tokens = s.split("[^a-z0-9]+");
        for (String k : keys) {
            String kk = k.trim();
            if (kk.contains(" ")) {
                if (s.contains(kk)) return true;
            } else {
                for (String t : tokens) {
                    if (t.equals(kk)) return true;
                }
            }
        }
        return false;
    }

    private String inferDate(String userPrompt) {
        if (userPrompt == null) return null;
        String s = userPrompt.toLowerCase(Locale.ROOT);

        if (s.contains("hôm nay") || s.contains("tối nay")
                || s.contains("sáng nay") || s.contains("chiều nay")) {
            return LocalDate.now().toString();
        }
        if (s.contains("hôm qua")) {
            return LocalDate.now().minusDays(1).toString();
        }

        s = s.replace("ngày", "ngay").replace("tháng", "thang");
        var m = java.util.regex.Pattern
                .compile("ngay\\s*(\\d{1,2})\\s*thang\\s*(\\d{1,2})")
                .matcher(s);

        if (m.find()) {
            int day = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int year = LocalDate.now().getYear();
            try {
                return LocalDate.of(year, month, day).toString();
            } catch (Exception ignored) {
            }
        }

        return null;
    }


    @Override
    public Page<AiChatHistoryResponse> getHistory(int page, int size) {
        User user = currentUserProvider.get();

        PageRequest pageable = PageRequest.of(
                page, size, Sort.by(Sort.Order.desc("createdAt"))
        );

        return historyRepo
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(h -> new AiChatHistoryResponse(
                        h.getId(),
                        h.getRole(),
                        h.getContent(),
                        h.getCreatedAt()
                ));
    }
}
