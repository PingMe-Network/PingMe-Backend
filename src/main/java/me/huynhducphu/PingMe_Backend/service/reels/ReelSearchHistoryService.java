package me.huynhducphu.PingMe_Backend.service.reels;

import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelSearchHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReelSearchHistoryService {
    void recordSearch(String query, Integer resultCount);
    Page<ReelSearchHistoryResponse> getMySearchHistory(Pageable pageable);
    void deleteById(Long id);
    void deleteAllMyHistory();
}
