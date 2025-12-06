package me.huynhducphu.PingMe_Backend.service.integration;

import me.huynhducphu.PingMe_Backend.service.integration.constant.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Admin 8/16/2025
 **/
public interface S3Service {
    String uploadFile(
            MultipartFile file, String key,
            boolean getUrl, long maxFileSize
    );

    String uploadFile(
            MultipartFile file, String folder,
            String fileName, boolean getUrl,
            long maxFileSize
    );

    String uploadCompressedFile(
            MultipartFile file, String folder,
            String fileName, boolean getUrl,
            long maxFileSize, MediaType mediaType
    ) throws IOException;

    void deleteFileByKey(String key);

    void deleteFileByUrl(String url);
}
