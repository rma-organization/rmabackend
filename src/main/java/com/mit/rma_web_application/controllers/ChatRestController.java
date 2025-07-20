// ChatRestController.java
package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.models.ChatMessageEntity;
import com.mit.rma_web_application.services.ChatService;
import com.mit.rma_web_application.repositories.ChatMessageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    // ✅ Constructor-based injection of both dependencies
    public ChatRestController(ChatService chatService, ChatMessageRepository chatMessageRepository) {
        this.chatService = chatService;
        this.chatMessageRepository = chatMessageRepository;
    }

    // Get all messages
    @GetMapping("/messages")
    public List<ChatMessageEntity> getMessages() {
        return chatService.getAllMessages();
    }

    // Get messages between two users
    @GetMapping("/messages/between")
    public List<ChatMessageEntity> getMessagesBetweenUsers(@RequestParam String sender,
                                                           @RequestParam String receiver) {
        return chatService.getMessagesBetweenUsers(sender, receiver);
    }

    // Get unread messages for a receiver
    @GetMapping("/messages/unread")
    public List<ChatMessageEntity> getUnreadMessages(@RequestParam String receiver) {
        return chatMessageRepository.findByReceiverAndIsReadFalse(receiver);
    }

    // Get unread message count between sender and receiver
    @GetMapping("/unread/count")
    public long getUnreadCount(@RequestParam String sender,
                               @RequestParam String receiver) {
        return chatService.getUnreadCount(sender, receiver);
    }

    // Mark messages as read
    @PostMapping("/markAsRead")
    public void markMessagesAsRead(@RequestParam String sender,
                                   @RequestParam String receiver) {
        chatService.markMessagesAsRead(sender, receiver);
    }
}
