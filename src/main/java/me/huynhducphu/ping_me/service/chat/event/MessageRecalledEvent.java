package me.huynhducphu.ping_me.service.chat.event;

import lombok.*;

/**
 * Admin 11/3/2025
 *
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRecalledEvent {

    private String messageId;
    private Long roomId;

}
