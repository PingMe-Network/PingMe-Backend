package me.huynhducphu.PingMe_Backend.service.reels.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelSearchHistoryResponse;
import me.huynhducphu.PingMe_Backend.model.reels.ReelSearchHistory;
import me.huynhducphu.PingMe_Backend.repository.ReelSearchHistoryRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.reels.ReelSearchHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReelSearchHistoryServiceImpl implements ReelSearchHistoryService {

    private final ReelSearchHistoryRepository repo;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public void recordSearch(String query, Integer resultCount) {
        try {
            var user = currentUserProvider.get();
            ReelSearchHistory h = ReelSearchHistory.builder()
                    .query(query)
                    .resultCount(resultCount)
                    .user(user)
                    .build();
            repo.save(h);
        } catch (Exception ignored) {
            // avoid breaking search if history saving fails
        }
    }

    @Override
    public Page<ReelSearchHistoryResponse> getMySearchHistory(Pageable pageable) {
        var user = currentUserProvider.get();
        var page = repo.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return page.map(this::toDto);
    }

    @Override
    public void deleteById(Long id) {
        var user = currentUserProvider.get();
        var opt = repo.findById(id);
        if (opt.isPresent()) {
            var rec = opt.get();
            if (rec.getUser() != null && rec.getUser().getId().equals(user.getId())) {
                repo.deleteById(id);
            } else {
                throw new jakarta.persistence.EntityNotFoundException("Không tìm thấy lịch sử hoặc không có quyền xóa");
            }
        } else {
            throw new jakarta.persistence.EntityNotFoundException("Không tìm thấy lịch sử");
        }
    }

    @Override
    public void deleteAllMyHistory() {
        var user = currentUserProvider.get();
        repo.deleteAllByUserId(user.getId());
    }

    private ReelSearchHistoryResponse toDto(ReelSearchHistory e) {
        var r = new ReelSearchHistoryResponse();
        r.setId(e.getId());
        r.setQuery(e.getQuery());
        r.setResultCount(e.getResultCount());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
