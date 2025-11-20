package me.huynhducphu.PingMe_Backend.model.music;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 3:41 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.model.music
 */

@Entity
@Table(name = "albums")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Album extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(columnDefinition = "VARCHAR(150)", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Artist albumOwner;

    @ManyToMany
    @JoinTable(
            name = "album_genre",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private Set<Genre> genres;

    @Column(nullable = false)
    private String coverImageUrl;

    @ManyToMany
    @JoinTable(
            name = "album_song",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    @ToString.Exclude
    private Set<Song> songs;

    @ManyToMany
    @JoinTable(
            name = "album_artist",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @ToString.Exclude
    private Set<Artist> featuredArtists;
}
