package me.huynhducphu.pingme_message_reactive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.pingme_message_reactive.dto.HistoryMessageResponse;
import me.huynhducphu.pingme_message_reactive.dto.MessageResponse;
import me.huynhducphu.pingme_message_reactive.dto.SendMessageRequest;
import me.huynhducphu.pingme_message_reactive.service.ReactiveMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ReactiveMessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MessageResponse> sendMessage(@RequestBody @Valid SendMessageRequest request) {
        return messageService.sendMessage(request);
    }

    @GetMapping("/history")
    public Mono<HistoryMessageResponse> getHistory(
            @RequestParam Long roomId,
            @RequestParam(required = false) String beforeId,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return messageService.getHistory(roomId, beforeId, size);
    }

    @DeleteMapping("/{id}/recall")
    public Mono<MessageResponse> recallMessage(@PathVariable String id) {
        return messageService.recallMessage(id);
    }
}
