package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.AlbumResponse;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 23/11/2025 - 5:39 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music
 */
public interface AlbumService {
    List<AlbumResponse> getAllAlbums();
}
