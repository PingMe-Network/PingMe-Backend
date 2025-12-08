package me.huynhducphu.PingMe_Backend.dto.response.music.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.huynhducphu.PingMe_Backend.model.music.Playlist;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {
    private Long id;
    private String name;
    private Boolean isPublic;

    public static PlaylistDto from(Playlist p) {
        return new PlaylistDto(p.getId(), p.getName(), p.getIsPublic());
    }
}
