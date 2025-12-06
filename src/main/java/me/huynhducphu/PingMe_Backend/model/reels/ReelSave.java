package me.huynhducphu.PingMe_Backend.model.reels;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

@Entity
@Table(
        name = "reel_saves",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reel_id", "user_id"})
)
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ReelSave extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ReelSave(Reel reel, User user) {
        this.reel = reel;
        this.user = user;
    }
}

