package me.huynhducphu.PingMe_Backend.model.music;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_songs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class FavoriteSong extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
