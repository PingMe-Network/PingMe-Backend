package org.ping_me.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.ping_me.model.chat.Message;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 9/04/2026, Thursday
 **/

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserChatEvent {
    Long senderId;
    String senderName;
    String message;
    long timestamp;
}
