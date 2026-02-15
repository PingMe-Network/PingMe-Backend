package me.huynhducphu.ping_me.model.reels;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.huynhducphu.ping_me.model.User;
import me.huynhducphu.ping_me.model.common.BaseEntity;
import me.huynhducphu.ping_me.model.constant.ReelStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reels")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Reel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String videoUrl;

    @Column(length = 200)
    String caption;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reel_hashtags", joinColumns = @JoinColumn(name = "reel_id"))
    @Column(name = "tag", length = 100)
    List<String> hashtags = new ArrayList<>();

    @Column(nullable = false)
    Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReelStatus status = ReelStatus.ACTIVE;

    @Column(length = 500)
    String adminNote;

    public Reel(String videoUrl, String caption) {
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.viewCount = 0L;
    }

    public Reel(String videoUrl, String caption, List<String> hashtags) {
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
        this.viewCount = 0L;
    }
}
