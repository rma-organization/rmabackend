package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.ChatMessage;
import com.mit.rma_web_application.models.ChatMessageEntity;
import com.mit.rma_web_application.repositories.ChatMessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatMessageRepository chatMessageRepository,
                          SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // 📩 Send a message to a specific user
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            SimpMessageHeaderAccessor headerAccessor) {

        String senderEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
        chatMessage.setSender(senderEmail); // override sender

        // Save to DB
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(senderEmail)
                .receiver(chatMessage.getReceiver())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageRepository.save(entity);

        // Send to specific user
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/queue/messages",
                chatMessage
        );
    }

    // Store user in session
    @MessageMapping("/chat.addUser")
    @SendToUser("/queue/messages")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("userEmail", chatMessage.getSender());
        return chatMessage;
    }
}
