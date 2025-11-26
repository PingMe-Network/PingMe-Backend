package me.huynhducphu.PingMe_Backend.service.music;

import me.huynhducphu.PingMe_Backend.dto.response.music.misc.TopSongPlayCounterDto;

import java.util.List;

public interface SongPlayHistoryService {
    @SuppressWarnings("unchecked")
    List<TopSongPlayCounterDto> getTopSongsTodayCached(int limit);

    @SuppressWarnings("unchecked")
    List<TopSongPlayCounterDto> getTopSongsThisWeekCached(int limit);

    @SuppressWarnings("unchecked")
    List<TopSongPlayCounterDto> getTopSongsThisMonthCached(int limit);
}
