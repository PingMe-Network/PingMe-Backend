package me.huynhducphu.PingMe_Backend.service.chat.event;

import lombok.*;

/**
 * Admin 11/3/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRecalledEvent {

    private Long messageId;
    private Long roomId;
    
}
