package org.ping_me.dto.request.chat.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Forward one existing message into another room.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForwardMessageRequest {

    @NotBlank(message = "Mã tin nhắn nguồn không được để trống")
    private String sourceMessageId;

    @NotBlank(message = "UUID không được để trống")
    private String clientMsgId;

    @NotNull(message = "Mã phòng đích không được để trống")
    private Long targetRoomId;
}
