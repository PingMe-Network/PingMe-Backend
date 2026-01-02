package me.huynhducphu.PingMe_Backend.controller.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.MarkReadRequest;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendWeatherMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.base.ApiResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.HistoryMessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.ReadStateResponse;
import me.huynhducphu.PingMe_Backend.service.chat.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin 8/26/2025
 *
 **/
@Tag(
        name = "Messages",
        description = "Các endpoints xử lý tin nhắn"
)
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestBody @Valid SendMessageRequest sendMessageRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.sendMessage(sendMessageRequest)));
    }

    @PostMapping("/files")
    public ResponseEntity<ApiResponse<MessageResponse>> sendFileMessage(
            @Valid @RequestPart(value = "message") SendMessageRequest sendMessageRequest,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.sendFileMessage(
                        sendMessageRequest,
                        file
                )));
    }

    @PostMapping("/weather")
    public ResponseEntity<ApiResponse<MessageResponse>> sendWeatherMessage(
            @RequestBody @Valid SendWeatherMessageRequest sendWeatherMessageRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.sendWeatherMessage(
                        sendWeatherMessageRequest
                )));
    }

    @DeleteMapping("/{id}/recall")
    public ResponseEntity<ApiResponse<MessageRecalledResponse>> recallMessage(
            @PathVariable String id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(messageService.recallMessage(id)));
    }

    @PostMapping("/read")
    public ResponseEntity<ApiResponse<ReadStateResponse>> markAsRead(
            @RequestBody @Valid MarkReadRequest markReadRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(messageService.markAsRead(markReadRequest)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<HistoryMessageResponse>> getHistoryMessages(
            @RequestParam Long roomId,
            @RequestParam(required = false) String beforeId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        var data = messageService.getHistoryMessages(roomId, beforeId, size);

        return ResponseEntity
                .ok(new ApiResponse<>(data));
    }
}
