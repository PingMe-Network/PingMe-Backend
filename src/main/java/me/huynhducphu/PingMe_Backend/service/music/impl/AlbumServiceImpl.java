package me.huynhducphu.PingMe_Backend.service.music.impl;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.response.music.AlbumResponse;
import me.huynhducphu.PingMe_Backend.dto.response.music.misc.AlbumSummaryDto;
import me.huynhducphu.PingMe_Backend.repository.music.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 23/11/2025 - 5:36 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music.impl
 */

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements me.huynhducphu.PingMe_Backend.service.music.AlbumService {
    private final AlbumRepository albumRepository;

    @Override
    public List<AlbumResponse> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(album -> new AlbumResponse(
                        album.getId(),
                        album.getTitle(),
                        album.getCoverImageUrl(),
                        album.getPlayCount()
                ))
                .toList();
    }
}
