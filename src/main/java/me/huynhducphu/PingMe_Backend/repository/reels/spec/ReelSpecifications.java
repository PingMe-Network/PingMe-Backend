package me.huynhducphu.PingMe_Backend.repository.reels.spec;
import me.huynhducphu.PingMe_Backend.dto.request.reels.AdminReelFilterRequest;
import me.huynhducphu.PingMe_Backend.model.reels.Reel;
import org.springframework.data.jpa.domain.Specification;

public class ReelSpecifications {

    public static Specification<Reel> byFilter(AdminReelFilterRequest f) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (f.getUserId() != null) {
                predicates.getExpressions().add(
                        cb.equal(root.get("user").get("id"), f.getUserId())
                );
            }

            if (f.getCaption() != null && !f.getCaption().isBlank()) {
                predicates.getExpressions().add(
                        cb.like(
                                cb.lower(root.get("caption")),
                                "%" + f.getCaption().trim().toLowerCase() + "%"
                        )
                );
            }

            if (f.getMinViews() != null) {
                predicates.getExpressions().add(
                        cb.greaterThanOrEqualTo(root.get("viewCount"), f.getMinViews())
                );
            }

            if (f.getMaxViews() != null) {
                predicates.getExpressions().add(
                        cb.lessThanOrEqualTo(root.get("viewCount"), f.getMaxViews())
                );
            }

            if (f.getFrom() != null) {
                predicates.getExpressions().add(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), f.getFrom())
                );
            }

            if (f.getTo() != null) {
                predicates.getExpressions().add(
                        cb.lessThanOrEqualTo(root.get("createdAt"), f.getTo())
                );
            }

            // if (f.getStatus() != null && !f.getStatus().isBlank()) {
            //     predicates.getExpressions().add(
            //             cb.equal(root.get("status"), ReelStatus.valueOf(f.getStatus().toUpperCase()))
            //     );
            // }

            return predicates;
        };
    }
}
