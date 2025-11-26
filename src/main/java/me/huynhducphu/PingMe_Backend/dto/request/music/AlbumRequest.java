package me.huynhducphu.PingMe_Backend.dto.request.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Le Tran Gia Huy
 * @created 25/11/2025 - 1:54 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.dto.request.music
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumRequest {
    private String title;
    private Long albumOwnerId;
    private Long playCount;
}