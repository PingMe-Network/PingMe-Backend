package me.huynhducphu.ping_me.service.integration.impl;

import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.ping_me.service.integration.constant.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Le Tran Gia Huy
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 */
@Slf4j
@Service
public class MediaCompressionServiceImpl implements me.huynhducphu.ping_me.service.integration.MediaCompressionService {

    private static final String FFMPEG_CMD = "ffmpeg";

    // Constants config
    private static final String AUDIO_BITRATE = "128k";
    private static final String VIDEO_CODEC = "libx264";
    private static final String AUDIO_CODEC_AAC = "aac";
    private static final String VIDEO_CRF = "28";
    private static final String VIDEO_PRESET = "fast";

    @Value("${app.ffmpeg.enabled:true}") // Mặc định true nếu quên config
    private boolean ffmpegEnabled;

    @Override
    public File compressMedia(MultipartFile originalFile, MediaType mediaType) {
        if (!ffmpegEnabled) {
            throw new UnsupportedOperationException("Chức năng nén đang bị vô hiệu hóa bởi Admin.");
        }

        return switch (mediaType) {
            case AUDIO -> processMedia(originalFile, ".mp3", this::buildAudioCommand);
            case VIDEO -> processMedia(originalFile, ".mp4", this::buildVideoCommand);
        };
    }

    /**
     * Hàm trung gian xử lý luồng: Tạo file tạm -> Chạy lệnh -> Dọn dẹp
     */
    private File processMedia(MultipartFile originalFile, String outputExtension,
                              BiFunction<String, String, List<String>> commandBuilder) {
        File tempInput = null;
        File tempOutput = null;

        try {
            // 1. Chuẩn bị file Input/Output
            tempInput = File.createTempFile("raw_", "_" + originalFile.getOriginalFilename());
            Files.copy(originalFile.getInputStream(), tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);

            tempOutput = File.createTempFile("compressed_", outputExtension);

            // 2. Lấy danh sách lệnh từ builder cụ thể
            List<String> command = commandBuilder.apply(tempInput.getAbsolutePath(), tempOutput.getAbsolutePath());

            // 3. Thực thi FFmpeg
            executeFfmpegCommand(command);

            return tempOutput;

        } catch (InterruptedException e) {
            // QUAN TRỌNG: Khôi phục trạng thái interrupt của thread
            Thread.currentThread().interrupt();
            
            log.error("Tiến trình nén bị ngắt quãng (Interrupted): {}", originalFile.getOriginalFilename());

            deleteFileSilent(tempOutput);

            throw new RuntimeException("Tiến trình xử lý media bị hủy bỏ", e);

        } catch (IOException e) {
            log.error("Lỗi I/O khi xử lý file [{}]: {}", originalFile.getOriginalFilename(), e.getMessage());

            deleteFileSilent(tempOutput);

            throw new RuntimeException("Lỗi đọc/ghi file hoặc lỗi thực thi FFmpeg", e);

        } finally {
            if (tempInput != null && tempInput.exists()) {
                if (!tempInput.delete()) log.warn("Không thể xóa file rác input: {}", tempInput.getAbsolutePath());
            }
        }
    }

    /**
     * Thực thi ProcessBuilder và bắt log
     */
    private void executeFfmpegCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Gộp Error stream vào Input stream

        Process process = processBuilder.start();

        // Đọc log để tránh buffer overflow (treo tiến trình)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Chỉ log debug để tránh spam console production
                log.debug("[FFmpeg]: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg exited with error code: " + exitCode);
        }
    }

    // --- Command Builders ---

    private List<String> buildAudioCommand(String inputPath, String outputPath) {
        return List.of(
                FFMPEG_CMD,
                "-y",
                "-i", inputPath,
                "-b:a", AUDIO_BITRATE,
                "-map", "0:a:0",
                outputPath
        );
    }

    private List<String> buildVideoCommand(String inputPath, String outputPath) {
        return List.of(
                FFMPEG_CMD,
                "-y",
                "-i", inputPath,
                "-c:v", VIDEO_CODEC,
                "-crf", VIDEO_CRF,
                "-preset", VIDEO_PRESET,
                "-c:a", AUDIO_CODEC_AAC,
                "-b:a", AUDIO_BITRATE,
                "-movflags", "+faststart",
                outputPath
        );
    }

    private void deleteFileSilent(File file) {
        if (file != null && file.exists()) {
            if (!file.delete()) {
                log.warn("Không thể xóa file tạm: {}", file.getAbsolutePath());
            }
        }
    }

}