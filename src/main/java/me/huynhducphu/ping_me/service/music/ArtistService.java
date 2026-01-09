package me.huynhducphu.ping_me.service.music;

import me.huynhducphu.ping_me.dto.request.music.ArtistRequest;
import me.huynhducphu.ping_me.dto.response.music.ArtistResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArtistService {
    List<ArtistResponse> getAllArtists();

    List<ArtistResponse> searchArtists(String name);

    ArtistResponse getArtistById(Long id);

    ArtistResponse saveArtist(ArtistRequest request, MultipartFile imgFile);

    ArtistResponse updateArtist(Long id, ArtistRequest request, MultipartFile imgFile);

    // Đổi tên deleteArtist thành softDeleteArtist (hoặc giữ nguyên tên tùy bạn)
    void softDeleteArtist(Long id);

    // Thêm 2 hàm này
    void restoreArtist(Long id);
    void hardDeleteArtist(Long id);
}