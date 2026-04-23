package org.ping_me.dto.response.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageSummaryResponse {
    private Long roomId;
    private Integer summarizedMessageCount;
    private String summary;
    private LocalDateTime generatedAt;
}
