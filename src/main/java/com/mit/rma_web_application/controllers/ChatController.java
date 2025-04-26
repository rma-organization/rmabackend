//package com.mit.rma_web_application.controllers;
//
//import com.mit.rma_web_application.dtos.ChatMessage;
//import com.mit.rma_web_application.models.ChatMessageEntity;
//import com.mit.rma_web_application.models.User;
//import com.mit.rma_web_application.repositories.ChatMessageRepository;
//import com.mit.rma_web_application.repositories.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalDateTime;
//
//@Controller
//@Slf4j
//public class ChatController {
//
//    private final ChatMessageRepository chatMessageRepository;
//    private final UserRepository userRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public ChatController(ChatMessageRepository chatMessageRepository,
//                          UserRepository userRepository,
//                          SimpMessagingTemplate messagingTemplate) {
//        this.chatMessageRepository = chatMessageRepository;
//        this.userRepository = userRepository;
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    // Send a message to a specific user
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage chatMessage,
//                            SimpMessageHeaderAccessor headerAccessor) {
//
//        // Get authenticated sender from session
//        String senderEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
//        if (senderEmail == null) {
//            log.error("Sender not authenticated");
//            return; // Return without sending if sender is not authenticated
//        }
//
//        // Verify receiver exists
//        User receiver = userRepository.findByEmail(chatMessage.getReceiver())
//                .orElse(null);
//
//        if (receiver == null) {
//            log.error("Receiver not found: {}", chatMessage.getReceiver());
//            return; // Return without sending if receiver doesn't exist
//        }
//
//        // Set sender and continue
//        chatMessage.setSender(senderEmail);
//
//        log.info("Sending message from {} to {}: {}", senderEmail, chatMessage.getReceiver(), chatMessage.getContent());
//
//        // Save to DB
//        ChatMessageEntity entity = ChatMessageEntity.builder()
//                .sender(senderEmail)
//                .receiver(chatMessage.getReceiver())
//                .content(chatMessage.getContent())
//                .type(chatMessage.getType())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        chatMessageRepository.save(entity);
//
//        // Send to specific user
//        messagingTemplate.convertAndSendToUser(
//                chatMessage.getReceiver(),
//                "/queue/messages",
//                chatMessage
//        );
//    }
//
//    // Store user in session
//    @MessageMapping("/chat.addUser")
//    public void addUser(@Payload ChatMessage chatMessage,
//                        SimpMessageHeaderAccessor headerAccessor) {
//        String senderEmail = chatMessage.getSender();
//
//        // Verify user exists in database
//        User user = userRepository.findByEmail(senderEmail)
//                .orElse(null);
//
//        if (user == null) {
//            log.error("User not found during connection: {}", senderEmail);
//            return;
//        }
//
//        log.info("User connected: {}", senderEmail);
//
//        // Store user email in session
//        headerAccessor.getSessionAttributes().put("userEmail", senderEmail);
//        headerAccessor.getSessionAttributes().put("userId", user.getId());
//
//        // Send confirmation back to the user who just connected
//        messagingTemplate.convertAndSendToUser(
//                senderEmail,
//                "/queue/messages",
//                chatMessage
//        );
//    }
//}
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

        String senderEmail = ((String) headerAccessor.getSessionAttributes().get("userEmail")).toLowerCase();

        // Validate sender
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderEmail));

        // Validate and normalize recipient
        String receiverEmail = chatMessage.getReceiver().trim().toLowerCase();
        User receiver = userRepository.findByEmail(receiverEmail)
                .filter(u -> u.getApprovalStatus() == ApprovalStatus.APPROVED)
                .orElseThrow(() -> new RuntimeException("Recipient not available: " + receiverEmail));

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        chatMessage.setTimestamp(now);

        // Persist message
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(senderEmail)
                .receiver(receiverEmail)
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .timestamp(now) // Use the same timestamp
                .build();
        chatMessageRepository.save(entity);

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                receiverEmail,
                "/queue/messages",
                chatMessage
        );

        // Send delivery receipt to sender
        ChatMessage receipt = new ChatMessage();
        receipt.setType(MessageType.STATUS);
        receipt.setContent("Delivered to " + receiverEmail);
        receipt.setTimestamp(now);
        messagingTemplate.convertAndSendToUser(
                senderEmail,
                "/queue/status",
                receipt
        );

        log.info("Message routed from {} to {}", senderEmail, receiverEmail);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String email = chatMessage.getSender().trim().toLowerCase();

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    headerAccessor.getSessionAttributes().put("userEmail", email);
                    headerAccessor.getSessionAttributes().put("userId", user.getId());
                    log.info("User session registered: {}", email);
                });
    }
}