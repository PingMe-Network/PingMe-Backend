package org.ping_me.service.ai.transcribe.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ping_me.service.ai.transcribe.SpeechToTextService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Le Tran Gia Huy
 * @created 16/02/2026 - 10:23 PM
 * @project PingMe-Backend
 * @package me.huynhducphu.ping_me.service.ai.transcribe
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SpeechToTextServiceImpl implements SpeechToTextService {
    @Value("${groq.ai.api.key}")
    private String apiKey;
    @Value("${groq.ai.api.url}")
    private String apiUrl;
    @Value("${app.ai.transcribe.max-audio-size}")
    private long maxAudioSize;
    private final RestClient restClient;

    public SpeechToTextServiceImpl() {
        this.restClient = RestClient.builder().build();
    }

    public String transcribeAudio(MultipartFile audioFile) throws IOException {
        validateAudioFile(audioFile);

        // 1. Chuẩn bị Body (Multipart)
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Resource từ file upload
        InputStreamResource fileResource = new InputStreamResource(audioFile.getInputStream()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename() != null ? audioFile.getOriginalFilename() : "audio.webm";
            }
        };

        body.add("file", fileResource);
        body.add("model", "whisper-large-v3");
        body.add("language", "vi"); // Tùy chọn: Gợi ý ngôn ngữ (nếu muốn force tiếng Việt)

        // 2. Gọi API Groq
        GroqResponse response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(GroqResponse.class);

        return response != null ? response.text() : "";
    }

    private void validateAudioFile(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("File audio không hợp lệ");
        }

        if (audioFile.getSize() > maxAudioSize) {
            throw new IllegalArgumentException("File audio vượt quá giới hạn cho phép");
        }

        String contentType = audioFile.getContentType();
        if (!StringUtils.hasText(contentType)) {
            return;
        }

        if (!contentType.startsWith("audio/") && !"video/webm".equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException("Định dạng file audio không được hỗ trợ");
        }
    }

    // Record class để map response JSON
    public record GroqResponse(String text) {}
}
