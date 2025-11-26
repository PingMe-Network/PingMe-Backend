package me.huynhducphu.PingMe_Backend.service.reels.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.reels.CreateReelRequest;
import me.huynhducphu.PingMe_Backend.dto.response.reels.ReelResponse;
import me.huynhducphu.PingMe_Backend.model.reels.Reel;
import me.huynhducphu.PingMe_Backend.model.reels.ReelLike;
import me.huynhducphu.PingMe_Backend.repository.reels.ReelCommentRepository;
import me.huynhducphu.PingMe_Backend.repository.reels.ReelLikeRepository;
import me.huynhducphu.PingMe_Backend.repository.reels.ReelRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.integration.S3Service;
import me.huynhducphu.PingMe_Backend.service.reels.ReelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReelServiceImpl implements ReelService {

    @Value("${app.reels.max-video-size}")
    private DataSize maxReelVideoSize;

    @Value("${app.reels.folder}")
    private String reelsFolder;

    private final ReelRepository reelRepository;
    private final ReelLikeRepository reelLikeRepository;
    private final ReelCommentRepository reelCommentRepository;

    private final CurrentUserProvider currentUserProvider;
    private final S3Service s3Service;
    private final ModelMapper modelMapper;

    @Override
    public ReelResponse createReel(CreateReelRequest dto, MultipartFile video) {
        var user = currentUserProvider.get();

        if (video == null || video.isEmpty())
            throw new IllegalArgumentException("Video không được bỏ trống");

        String contentType = video.getContentType();
        if (contentType == null || !contentType.startsWith("video/"))
            throw new IllegalArgumentException("File upload phải là video");

        String original = video.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : ".mp4";

        String randomFileName = UUID.randomUUID() + ext;
        long maxBytes = maxReelVideoSize.toBytes();

        String url = s3Service.uploadFile(
                video, reelsFolder, randomFileName, true, maxBytes
        );

        var reel = new Reel(url, dto.getCaption());
        reel.setUser(user);

        var saved = reelRepository.saveAndFlush(reel);
        return toReelResponse(saved, user.getId());
    }

    @Override
    public Page<ReelResponse> getFeed(Pageable pageable) {
        var me = currentUserProvider.get();

        return reelRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(reel -> toReelResponse(reel, me.getId()));
    }

    @Override
    public ReelResponse incrementView(Long reelId) {
        var me = currentUserProvider.get();

        var reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Reel"));

        reel.setViewCount(reel.getViewCount() + 1);
        var saved = reelRepository.save(reel);

        return toReelResponse(saved, me.getId());
    }

    @Override
    public ReelResponse toggleLike(Long reelId) {
        var me = currentUserProvider.get();

        var reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Reel"));

        boolean liked = reelLikeRepository.existsByReelIdAndUserId(reelId, me.getId());

        if (liked) {
            reelLikeRepository.deleteByReelIdAndUserId(reelId, me.getId());
        } else {
            reelLikeRepository.save(new ReelLike(reel, me));
        }

        // trả response sau toggle
        return toReelResponse(reel, me.getId());
    }

    @Override
    public void deleteReel(Long id) {
        var user = currentUserProvider.get();

        var reel = reelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Reel"));

        if (!reel.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("Bạn không có quyền xóa Reel này");

        if (reel.getVideoUrl() != null)
            s3Service.deleteFileByUrl(reel.getVideoUrl());

        reelRepository.delete(reel);
    }

    private ReelResponse toReelResponse(Reel reel, Long meId) {
        ReelResponse res = modelMapper.map(reel, ReelResponse.class);

        long likeCount = reelLikeRepository.countByReelId(reel.getId());
        long commentCount = reelCommentRepository.countByReelId(reel.getId());
        boolean isLikedByMe = reelLikeRepository.existsByReelIdAndUserId(reel.getId(), meId);

        res.setLikeCount(likeCount);
        res.setCommentCount(commentCount);
        res.setIsLikedByMe(isLikedByMe);

        res.setUserId(reel.getUser().getId());
        res.setUserName(reel.getUser().getName());
        res.setUserAvatarUrl(reel.getUser().getAvatarUrl());
        return res;
    }
}
