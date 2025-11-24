package me.huynhducphu.PingMe_Backend.dto.response.music.misc;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSongPlayCounterDto {
    private Long songId;
    private String title;
    private String imgUrl;
    private Long playCount; // số lần nghe trong khoảng thời gian
}
