package me.huynhducphu.PingMe_Backend.service.integration;

import org.springframework.web.multipart.MultipartFile;

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

    void deleteFileByKey(String key);

    void deleteFileByUrl(String url);
}
