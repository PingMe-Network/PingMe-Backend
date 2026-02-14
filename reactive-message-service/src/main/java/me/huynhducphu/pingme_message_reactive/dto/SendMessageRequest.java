package me.huynhducphu.pingme_message_reactive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import me.huynhducphu.pingme_message_reactive.model.MessageType;

@Data
public class SendMessageRequest {

    @NotNull
    private Long roomId;

    @NotNull
    private Long senderId;

    @NotBlank
    private String content;

    @NotNull
    private MessageType type;

    @NotBlank
    private String clientMsgId;
}
