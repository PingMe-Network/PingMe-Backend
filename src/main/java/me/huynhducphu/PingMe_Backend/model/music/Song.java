package me.huynhducphu.PingMe_Backend.model.music;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 3:39 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.model.music
 */

@Entity
@Table(name = "songs")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Song extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(columnDefinition = "VARCHAR(150)", nullable = false)
    private String title;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SongArtistRole> artistRoles;

    @ManyToMany(mappedBy = "songs")
    @ToString.Exclude
    private Set<Album> albums;

    @ManyToMany
    @JoinTable(
            name = "song_genre",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private Set<Genre> genres;

    @Column(nullable = false)
    private int duration; //tính bằng giây

    @Column(nullable = false)
    private String songUrl;

    @Column(nullable = false)
    private String imgUrl;
}
