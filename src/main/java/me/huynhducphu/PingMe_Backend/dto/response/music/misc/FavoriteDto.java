package me.huynhducphu.PingMe_Backend.dto.response.music.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.huynhducphu.PingMe_Backend.model.music.FavoriteSong;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDto {
    private Long id;
    private Long songId;
    private String title;

    public static FavoriteDto from(FavoriteSong fs) {
        return new FavoriteDto(fs.getId(), fs.getSong().getId(), fs.getSong().getTitle());
    }

}