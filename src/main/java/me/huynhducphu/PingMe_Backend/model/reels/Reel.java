package me.huynhducphu.PingMe_Backend.model.reels;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;
import me.huynhducphu.PingMe_Backend.model.constant.ReelStatus;

@Entity
@Table(name = "reels")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Reel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String videoUrl;

    @Column(length = 200)
    private String caption;

    // Optional: hashtags stored as a single string (e.g. "#fun,#travel")
    @Column(name = "hashtags", length = 500)
    private String hashtags;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReelStatus status = ReelStatus.ACTIVE;

    @Column(length = 500)
    private String adminNote;

    public Reel(String videoUrl, String caption) {
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.viewCount = 0L;
    }

    public Reel(String videoUrl, String caption, String hashtags) {
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.hashtags = hashtags;
        this.viewCount = 0L;
    }
}
