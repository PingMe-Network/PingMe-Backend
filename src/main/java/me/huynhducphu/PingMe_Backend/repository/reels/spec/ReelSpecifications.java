package me.huynhducphu.PingMe_Backend.repository.reels.spec;

import me.huynhducphu.PingMe_Backend.dto.request.reels.AdminReelFilterRequest;
import me.huynhducphu.PingMe_Backend.model.reels.Reel;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ReelSpecifications {

    public static Specification<Reel> byFilter(AdminReelFilterRequest f) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (f.getUserId() != null) {
                predicates.add(
                        cb.equal(root.get("user").get("id"), f.getUserId())
                );
            }

            if (f.getCaption() != null && !f.getCaption().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("caption")),
                                "%" + f.getCaption().trim().toLowerCase() + "%"
                        )
                );
            }

            if (f.getMinViews() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("viewCount"), f.getMinViews())
                );
            }

            if (f.getMaxViews() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("viewCount"), f.getMaxViews())
                );
            }

            if (f.getFrom() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), f.getFrom())
                );
            }

            if (f.getTo() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("createdAt"), f.getTo())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
