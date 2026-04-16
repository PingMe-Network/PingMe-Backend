package org.ping_me.service.chat.event.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ping_me.model.chat.Message;

/**
 * Admin 4/16/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageUpdatedEvent {

    private Message message;
}
