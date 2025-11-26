package me.huynhducphu.PingMe_Backend.repository.reels;

import me.huynhducphu.PingMe_Backend.model.reels.ReelCommentReaction;
import me.huynhducphu.PingMe_Backend.model.constant.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReelCommentReactionRepository extends JpaRepository<ReelCommentReaction, Long> {
    Optional<ReelCommentReaction> findByCommentIdAndUserId(Long commentId, Long userId);
    long countByCommentId(Long commentId);
    long countByCommentIdAndType(Long commentId, ReactionType type);
}
