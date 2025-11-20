package me.huynhducphu.PingMe_Backend.model.music;

import jakarta.persistence.*;
import lombok.*;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 3:48 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.model.music
 */

@Entity
@Table(name = "genres")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Genre extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "genres")
    @ToString.Exclude
    private List<Song> songs;


    @ManyToMany(mappedBy = "genres")
    @ToString.Exclude
    private List<Album> albums;
}
