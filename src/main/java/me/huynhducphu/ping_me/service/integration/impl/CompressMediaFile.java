package me.huynhducphu.ping_me.service.integration.impl;

import me.huynhducphu.ping_me.service.integration.constant.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Le Tran Gia Huy
 * @created 28/11/2025 - 7:04 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.service.compress.media.song
 */

@Component
public class CompressMediaFile {
    private static final String FFMPEG_PATH = "ffmpeg";

    @Value("${app.ffmpeg.enabled}")
    private boolean ffmpegEnabled;

    public File compressMedia(MultipartFile originalFile, MediaType mediaType) throws IOException {
        if (!ffmpegEnabled)
            throw new IllegalArgumentException("Chức năng này đã bị vô hiệu hóa. Hãy liên hệ admin");

        if (mediaType.equals(MediaType.AUDIO))
            return compressAudio(originalFile);
        else if (mediaType.equals(MediaType.VIDEO))
            return compressVideo(originalFile);
        else
            throw new IllegalArgumentException("Unsupported media type: " + mediaType);

    }

    /**
     * Nén Audio về định dạng MP3 128kbps
     */
    private File compressAudio(MultipartFile originalFile) {
        File tempInput = null;
        File tempOutput = null;

        try {
            // 1. Tạo file tạm đầu vào (Input) từ MultipartFile
            tempInput = File.createTempFile("input_", "_" + originalFile.getOriginalFilename());
            Files.copy(originalFile.getInputStream(), tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. Tạo file tạm đầu ra (Output) - Đuôi .mp3
            tempOutput = File.createTempFile("output_", ".mp3");

            // 3. Xây dựng câu lệnh FFmpeg
            // Lệnh tương đương: ffmpeg -y -i input.wav -b:a 128k -map 0:a:0 output.mp3
            List<String> command = new ArrayList<>();
            command.add(FFMPEG_PATH);

            command.add("-y"); // Tự động ghi đè nếu file output đã tồn tại

            command.add("-i"); // Input flag
            command.add(tempInput.getAbsolutePath()); // Đường dẫn file gốc

            command.add("-b:a"); // Bitrate Audio
            command.add("128k"); // 128kbps (Mức nén chuẩn cho Web, giảm dung lượng rất tốt)

            // (Optional) Map chỉ lấy stream audio đầu tiên (tránh lỗi nếu file gốc có cover art dạng video)
            command.add("-map");
            command.add("0:a:0");

            command.add(tempOutput.getAbsolutePath()); // Đường dẫn file đích

            // 4. Chạy lệnh bằng ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Gộp luồng lỗi (ErrorStream) vào luồng đầu ra (InputStream) để dễ đọc log
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // 5. Đọc log của FFmpeg (Quan trọng: Nếu không đọc, buffer đầy sẽ gây treo tiến trình)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while (reader.readLine() != null) {
                    // Bạn có thể log ra console để debug nếu muốn
                    // System.out.println("[FFmpeg]: " + line);
                }
            }

            // 6. Đợi tiến trình chạy xong
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg nén lỗi. Exit code: " + exitCode);
            }

            return tempOutput; // Trả về file đã nén

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Tiến trình nén bị ngắt quãng");
        } catch (Exception e) {
            // Nếu lỗi thì xóa luôn file output cho sạch
            if (tempOutput != null && tempOutput.exists()) tempOutput.delete();
            throw new RuntimeException("Lỗi nén Audio: " + e.getMessage());
        } finally {
            // Luôn luôn xóa file input tạm để dọn rác
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
        }
    }


    public File compressVideo(MultipartFile originalFile) throws IOException {

        File tempInput = null;
        File tempOutput = null;

        try {
            // 1. Tạo file tạm đầu vào
            tempInput = File.createTempFile("video_input_", "_" + originalFile.getOriginalFilename());
            Files.copy(originalFile.getInputStream(), tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. SỬA: File đầu ra phải là .mp4
            tempOutput = File.createTempFile("video_output_", ".mp4");

            // 3. Xây dựng câu lệnh FFmpeg cho Video
            List<String> command = new ArrayList<>();
            command.add(FFMPEG_PATH); // Biến path lấy từ config

            command.add("-y"); // Ghi đè file nếu tồn tại
            command.add("-i");
            command.add(tempInput.getAbsolutePath());

            // --- CẤU HÌNH VIDEO ---
            command.add("-c:v");
            command.add("libx264"); // Codec H.264 (Chuẩn nhất cho mọi trình duyệt)

            command.add("-crf");
            command.add("28");
            // CRF (Chất lượng):
            // 18 = Rất nét (file nặng)
            // 23 = Chuẩn mặc định
            // 28 = Nén khá mạnh (Phù hợp cho Reels/Web, dung lượng giảm nhiều)

            command.add("-preset");
            command.add("fast");
            // Tốc độ nén: ultrafast, superfast, veryfast, faster, fast, medium, slow...
            // 'fast' là cân bằng tốt giữa thời gian chờ và dung lượng.

            // (Optional) Resize video nếu quá to (ví dụ ép về chiều cao 720p, chiều ngang tự tính)
            // command.add("-vf");
            // command.add("scale=-2:720");

            // --- CẤU HÌNH AUDIO ---
            command.add("-c:a");
            command.add("aac"); // Codec âm thanh chuẩn cho MP4

            command.add("-b:a");
            command.add("128k"); // Bitrate âm thanh 128kbps

            // --- TỐI ƯU CHO WEB (Quan trọng) ---
            command.add("-movflags");
            command.add("+faststart");
            // Giúp video chạy được ngay khi load được 1 ít (giống YouTube), ko cần tải hết file.

            command.add(tempOutput.getAbsolutePath());

            // 4. Chạy lệnh
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 5. Đọc log (Bắt buộc để tránh treo tiến trình)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg nén video lỗi. Exit code: " + exitCode);
            }

            return tempOutput;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Tiến trình nén bị ngắt quãng");
        } catch (Exception e) {
            if (tempOutput != null && tempOutput.exists()) tempOutput.delete();
            throw new RuntimeException("Lỗi nén Video: " + e.getMessage());
        } finally {
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
        }
    }
}
