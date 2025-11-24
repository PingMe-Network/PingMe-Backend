package me.huynhducphu.PingMe_Backend.repository.music;

import me.huynhducphu.PingMe_Backend.model.music.SongPlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongPlayHistoryRepository extends JpaRepository<SongPlayHistory, Long> {

}
