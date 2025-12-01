package me.huynhducphu.PingMe_Backend.service.reels;

import me.huynhducphu.PingMe_Backend.dto.request.reels.ReelRequest;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReelService {
    ReelResponse createReel(ReelRequest dto, MultipartFile video);

    Page<ReelResponse> getFeed(Pageable pageable);

    ReelResponse incrementView(Long reelId);

    ReelResponse toggleLike(Long reelId);

    void deleteReel(Long id);

    ReelResponse updateReel(Long reelId, ReelRequest dto, MultipartFile video);
}
