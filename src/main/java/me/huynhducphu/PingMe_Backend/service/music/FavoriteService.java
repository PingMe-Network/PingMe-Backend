package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.misc.FavoriteDto;

import java.util.List;

public interface FavoriteService {
    List<FavoriteDto> getFavorites();

    void addFavorite(Long songId);

    void removeFavorite(Long songId);

    boolean isFavorite(Long songId);
}
