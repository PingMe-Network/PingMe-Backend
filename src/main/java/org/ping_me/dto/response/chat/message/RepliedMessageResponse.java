package org.ping_me.dto.response.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ping_me.model.constant.MessageType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RepliedMessageResponse {
    private String id;
    private Long senderId;
    private String content;
    private MessageType type;
    private Boolean isActive;
    private String fileFormat;
    private List<String> mediaUrls;
}
