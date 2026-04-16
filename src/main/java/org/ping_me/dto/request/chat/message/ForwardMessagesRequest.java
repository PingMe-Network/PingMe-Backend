package org.ping_me.dto.request.chat.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Forward one existing message into multiple rooms.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForwardMessagesRequest {

    @NotBlank(message = "Mã tin nhắn nguồn không được để trống")
    private String sourceMessageId;

    @NotBlank(message = "UUID không được để trống")
    private String clientMsgId;

    @NotEmpty(message = "Danh sách phòng đích không được để trống")
    private List<@NotNull(message = "Mã phòng đích không hợp lệ") Long> targetRoomIds;
}
