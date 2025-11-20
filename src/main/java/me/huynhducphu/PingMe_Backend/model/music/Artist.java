package me.huynhducphu.PingMe_Backend.model.music;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 3:54 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.model.music
 */

@Entity
@Table(name = "artists")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Artist extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(columnDefinition = "VARCHAR(150)", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "artist")
    @ToString.Exclude
    private List<SongArtistRole> songRoles;

    @OneToMany(mappedBy = "albumOwner")
    @ToString.Exclude
    private List<Album> ownAlbums;

    @ManyToMany(mappedBy = "featuredArtists")
    @ToString.Exclude
    private Set<Album> albums;

    @Column(nullable = false)
    private String imgUrl;
}
