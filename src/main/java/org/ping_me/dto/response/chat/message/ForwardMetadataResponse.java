package org.ping_me.dto.response.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForwardMetadataResponse {
    private String sourceMessageId;
    private Long sourceRoomId;
    private Long sourceSenderId;
}
