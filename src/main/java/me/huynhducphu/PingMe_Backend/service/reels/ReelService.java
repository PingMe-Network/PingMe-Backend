package me.huynhducphu.PingMe_Backend.service.reels;

import me.huynhducphu.PingMe_Backend.dto.request.reels.CreateReelRequest;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReelService {
    ReelResponse createReel(CreateReelRequest dto, MultipartFile video);

    Page<ReelResponse> getFeed(Pageable pageable);

    ReelResponse incrementView(Long reelId);

    ReelResponse toggleLike(Long reelId);

    void deleteReel(Long id);
}
