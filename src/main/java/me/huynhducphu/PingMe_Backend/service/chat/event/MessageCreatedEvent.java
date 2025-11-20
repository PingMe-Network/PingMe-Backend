package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.*;
import me.huynhducphu.PingMe_Backend.model.Message;

/**
 * Admin 8/29/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageCreatedEvent {

    private Message message;

}
