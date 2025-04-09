package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.models.ChatMessageEntity;
import com.mit.rma_web_application.services.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/messages")
    public List<ChatMessageEntity> getMessages() {
        return chatService.getAllMessages();
    }

    @GetMapping("/messages/between")
    public List<ChatMessageEntity> getMessagesBetweenUsers(@RequestParam String sender,
                                                           @RequestParam String receiver) {
        return chatService.getMessagesBetweenUsers(sender, receiver);
    }
}
