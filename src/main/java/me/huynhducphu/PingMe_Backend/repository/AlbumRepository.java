package me.huynhducphu.PingMe_Backend.repository;

import me.huynhducphu.PingMe_Backend.model.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
*@created 20/11/2025 - 6:21 PM
*@project DHKTPM18ATT_Nhom10_PingMe_Backend
*@package me.huynhducphu.PingMe_Backend.repository
*@author Le Tran Gia Huy
*/
@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
}
