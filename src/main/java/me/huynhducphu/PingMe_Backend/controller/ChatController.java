package me.huynhducphu.PingMe_Backend.controller;

import me.huynhducphu.PingMe_Backend.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Admin 8/11/2025
 **/
@Controller
public class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(
            ChatMessage chatMessage,
            Principal principal
    ) {
        System.out.println(principal);

        return chatMessage;
    }

}
