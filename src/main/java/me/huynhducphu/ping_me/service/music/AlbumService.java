package me.huynhducphu.ping_me.service.music;

import me.huynhducphu.ping_me.dto.request.music.AlbumRequest;
import me.huynhducphu.ping_me.dto.response.music.AlbumResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 23/11/2025 - 5:39 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music
 */
public interface AlbumService {
    Long MAX_COVER_SIZE = 5L * 1024L * 1024L;

    List<AlbumResponse> getAllAlbums();

    AlbumResponse getAlbumById(Long id);

    List<AlbumResponse> getAlbumByTitleContainIgnoreCase(String title);

    AlbumResponse save(AlbumRequest albumRequestDto, MultipartFile albumCoverImg);

    AlbumResponse update(Long albumId, AlbumRequest albumRequestDto, MultipartFile albumCoverImg);

    void softDelete(Long id);

    void hardDelete(Long id);

    void restore(Long id);
}
