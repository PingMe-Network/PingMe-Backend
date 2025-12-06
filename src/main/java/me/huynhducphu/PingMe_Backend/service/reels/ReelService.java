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

    ReelResponse toggleSave(Long reelId);

    void deleteReel(Long id);

    ReelResponse updateReel(Long reelId, ReelRequest dto, MultipartFile video);

    // List reels liked by the current user
    Page<ReelResponse> getLikedReels(Pageable pageable);

    // List reels saved/favorited by the current user
    Page<ReelResponse> getSavedReels(Pageable pageable);

    // List reels viewed by the current user
    Page<ReelResponse> getViewedReels(Pageable pageable);

    // Search reels by title/caption
    Page<ReelResponse> searchByTitle(String query, Pageable pageable);
}
