package me.huynhducphu.PingMe_Backend.dto.request.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Le Tran Gia Huy
 * @created 20/11/2025 - 10:19 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.dto.request.music
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongRequest {
    private String title;

    private int duration; // tính bằng giây

    private Long mainArtistId; // ID của nghệ sĩ chính

    private Long[] featuredArtistIds; // IDs của các nghệ sĩ phụ

    private Long[] genreIds; // Tên các thể loại

    private Long[] albumId; // ID của album (nếu có)

}
