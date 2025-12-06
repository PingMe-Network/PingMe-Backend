package me.huynhducphu.PingMe_Backend.service.reels;

import me.huynhducphu.PingMe_Backend.dto.request.reels.AdminReelFilterRequest;
import me.huynhducphu.PingMe_Backend.dto.response.reels.AdminReelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReelService {
    Page<AdminReelResponse> getReels(AdminReelFilterRequest filter, Pageable pageable);

    AdminReelResponse getDetail(Long reelId);

    AdminReelResponse updateCaption(Long reelId, String caption);

    AdminReelResponse hideReel(Long reelId, String adminNote);

    void hardDelete(Long reelId);
}
