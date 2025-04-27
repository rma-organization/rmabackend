package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.ChatMessage;
import com.mit.rma_web_application.models.*;
import com.mit.rma_web_application.repositories.ChatMessageRepository;
import com.mit.rma_web_application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            SimpMessageHeaderAccessor headerAccessor) {

        String senderUserName = ((String) headerAccessor.getSessionAttributes().get("username")).toLowerCase();

        // Validate sender
        User sender = userRepository.findByUsername(senderUserName)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderUserName));

        // Validate and normalize recipient
        String receiverUserName = chatMessage.getReceiver().trim().toLowerCase();
        User receiver = userRepository.findByUsername(receiverUserName)
                .filter(u -> u.getApprovalStatus() == ApprovalStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("Recipient not available: " + receiverUserName));

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        chatMessage.setTimestamp(now);

        // Persist message
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(senderUserName)
                .receiver(receiverUserName)
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .timestamp(now)
                .build();
        chatMessageRepository.save(entity);

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                receiverUserName,
                "/queue/messages",
                chatMessage
        );

        // Send delivery receipt to sender
        ChatMessage receipt = new ChatMessage();
        receipt.setType(MessageType.STATUS);
        receipt.setContent("Delivered to " + receiverUserName);
        receipt.setTimestamp(now);
        messagingTemplate.convertAndSendToUser(
                senderUserName,
                "/queue/status",
                receipt
        );

        log.info("Message sent from '{}' to '{}'", senderUserName, receiverUserName);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender().trim().toLowerCase();

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    headerAccessor.getSessionAttributes().put("username", username); // <-- fixed key here
                    headerAccessor.getSessionAttributes().put("userId", user.getId());
                    log.info("User '{}' added to WebSocket session.", username);
                });
    }
}
