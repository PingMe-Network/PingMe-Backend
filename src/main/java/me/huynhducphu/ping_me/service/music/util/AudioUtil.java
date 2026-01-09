package me.huynhducphu.ping_me.service.music.util;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author Le Tran Gia Huy
 * @created 25/11/2025 - 1:49 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.music.util
 */

@Component
public class AudioUtil {
    public int getDurationFromMusicFile(MultipartFile file) {
        File tempFile = null;
        try {
            // 1. Tạo file tạm thời trên server
            tempFile = File.createTempFile("temp_audio_", "_" + file.getOriginalFilename());

            // 2. Copy dữ liệu từ MultipartFile sang file tạm
            Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 3. Dùng thư viện JAudioTagger để đọc header
            AudioFile audioFile = AudioFileIO.read(tempFile);
            int duration = audioFile.getAudioHeader().getTrackLength(); // Trả về số giây (int)

            return duration;

        } catch (Exception e) {
            // Log lỗi nếu cần
            System.err.println("Lỗi khi đọc duration: " + e.getMessage());
            return 0; // Nếu lỗi thì mặc định là 0 giây (hoặc throw exception tùy bạn)
        } finally {
            // 4. Quan trọng: Xóa file tạm sau khi dùng xong để tránh rác server
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
