package me.huynhducphu.PingMe_Backend.controller.music.misc;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.constant.ArtistRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Le Tran Gia Huy
 * @created 27/11/2025 - 6:11 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.controller.music.misc
 */

@RestController
@RequestMapping("/music/common")
@RequiredArgsConstructor
public class CommonController {
    @GetMapping("/roles")
    public ResponseEntity<ArtistRole[]> getArtistRoles() {
        // Hàm .values() của Enum trả về mảng tất cả các giá trị
        return ResponseEntity.ok(ArtistRole.values());
    }
}
