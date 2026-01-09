package me.huynhducphu.ping_me.service.ffmpeg;

import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.ping_me.service.ffmpeg.constants.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Le Tran Gia Huy
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 */
@Slf4j
@Service
public class FFMPEGService {

    private static final String FFMPEG_CMD = "ffmpeg";

    // Constants config
    private static final String AUDIO_BITRATE = "128k";
    private static final String VIDEO_CODEC = "libx264";
    private static final String AUDIO_CODEC_AAC = "aac";
    private static final String VIDEO_CRF = "28";
    private static final String VIDEO_PRESET = "fast";

    @Value("${app.ffmpeg.enabled:true}")
    private boolean ffmpegEnabled;

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
            // --- BƯỚC 1: CẤU HÌNH FILE AN TOÀN (FIX SONAR S5443 & S6549) ---

            // A. Lấy thư mục tạm an toàn (Secure Directory)
            File secureDir = getSecureTempDir();

            // B. Lấy Extension an toàn (Không dùng tên file gốc để nối chuỗi)
            String originalName = originalFile.getOriginalFilename();
            String inputExtension = ".tmp";
            if (originalName != null && originalName.contains(".")) {
                inputExtension = originalName.substring(originalName.lastIndexOf("."));
            }

            // C. Tạo file Input và Output TRONG thư mục an toàn
            // createTempFile sẽ tự sinh tên ngẫu nhiên (ví dụ: raw_12345.mp4) -> Chống trùng lặp & đoán tên
            tempInput = File.createTempFile("raw_", inputExtension, secureDir);
            Files.copy(originalFile.getInputStream(), tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);

            tempOutput = File.createTempFile("compressed_", outputExtension, secureDir);

            // --- BƯỚC 2: XỬ LÝ FFMPEG ---

            // Lấy danh sách lệnh từ builder cụ thể
            List<String> command = commandBuilder.apply(tempInput.getAbsolutePath(), tempOutput.getAbsolutePath());

            // Thực thi
            executeFfmpegCommand(command);

            return tempOutput;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Tiến trình nén bị ngắt quãng: {}", originalFile.getOriginalFilename());
            deleteFileSilent(tempOutput);
            throw new RuntimeException("Tiến trình xử lý media bị hủy bỏ", e);

        } catch (IOException e) {
            log.error("Lỗi I/O khi xử lý file [{}]: {}", originalFile.getOriginalFilename(), e.getMessage());
            deleteFileSilent(tempOutput);
            throw new RuntimeException("Lỗi đọc/ghi file hoặc lỗi thực thi FFmpeg", e);

        } finally {
            // Clean code: Dùng hàm helper để xóa, không viết lại logic check null
            deleteFileSilent(tempInput);
        }
    }

    /**
     * Thực thi ProcessBuilder và bắt log
     */
    private void executeFfmpegCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Đọc log để tránh buffer overflow
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
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
                FFMPEG_CMD, "-y", "-i", inputPath,
                "-b:a", AUDIO_BITRATE,
                "-map", "0:a:0",
                outputPath
        );
    }

    private List<String> buildVideoCommand(String inputPath, String outputPath) {
        return List.of(
                FFMPEG_CMD, "-y", "-i", inputPath,
                "-c:v", VIDEO_CODEC, "-crf", VIDEO_CRF, "-preset", VIDEO_PRESET,
                "-c:a", AUDIO_CODEC_AAC, "-b:a", AUDIO_BITRATE,
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

    /**
     * Tạo thư mục tạm an toàn, chỉ Owner mới có quyền truy cập
     */
    private File getSecureTempDir() throws IOException {
        String systemTemp = System.getProperty("java.io.tmpdir");
        Path path = Paths.get(systemTemp, "pingme-ffmpeg-temp");

        // Chỉ tạo mới nếu chưa tồn tại
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            File file = path.toFile();

            // Thực hiện set quyền và lấy kết quả trả về
            boolean r = file.setReadable(true, true);
            boolean w = file.setWritable(true, true);
            boolean x = file.setExecutable(true, true);

            // Nếu bất kỳ quyền nào set thất bại
            if (!r || !w || !x) {
                // 1. Cố gắng xóa thư mục vừa tạo để không để lại rác không an toàn
                if (file.exists() && !file.delete()) {
                    log.warn(
                            "CẢNH BÁO: Không thể xóa thư mục tạm không an toàn: {}",
                            file.getAbsolutePath()
                    );
                }

                // 2. Ném lỗi để dừng quy trình
                throw new IOException(
                        "Lỗi bảo mật: Không thể thiết lập quyền hạn chế (700) cho thư mục tạm: " + file.getAbsolutePath()
                );
            }
        }
        return path.toFile();
    }
}