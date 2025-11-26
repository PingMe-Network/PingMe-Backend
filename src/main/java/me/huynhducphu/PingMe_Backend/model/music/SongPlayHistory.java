package me.huynhducphu.PingMe_Backend.model.music;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "song_play_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongPlayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ai nghe bài hát
    @Column(nullable = false)
    private Long userId;

    // Bài hát nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    // Thời điểm nghe
    @Column(nullable = false)
    private LocalDateTime playedAt;


}
