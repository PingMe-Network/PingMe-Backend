package me.huynhducphu.ping_me.service.integration;

import me.huynhducphu.ping_me.service.integration.constant.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Admin 1/9/2026
 *
 **/
public interface MediaCompressionService {
    File compressMedia(MultipartFile originalFile, MediaType mediaType);
}
