package me.huynhducphu.ping_me.service.music.util;

import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author Le Tran Gia Huy
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 */
@Slf4j
@Component
public class AudioUtil {

    public int getDurationFromMusicFile(MultipartFile file) {
        File tempFile = null;
        try {
            // 1. Tạo file tạm
            tempFile = File.createTempFile("temp_audio_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. Đọc thông tin file
            AudioFile audioFile = AudioFileIO.read(tempFile);

            // 3. Trả về duration
            return audioFile.getAudioHeader().getTrackLength();

        } catch (Exception e) {
            log.error("Lỗi khi đọc duration file [{}]: {}", file.getOriginalFilename(), e.getMessage());
            return 0;
        } finally {
            // 4. Clean up file tạm
            deleteTempFile(tempFile);
        }
    }

    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted)
                log.warn("CẢNH BÁO: Không thể xóa file tạm: {}", file.getAbsolutePath());
        }
    }
}