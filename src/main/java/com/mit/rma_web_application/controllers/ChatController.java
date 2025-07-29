//package com.mit.rma_web_application.controllers;
//
//import com.mit.rma_web_application.dtos.ChatMessage;
//import com.mit.rma_web_application.models.*;
//import com.mit.rma_web_application.repositories.ChatMessageRepository;
//import com.mit.rma_web_application.repositories.UserRepository;
//import com.mit.rma_web_application.services.ChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.time.LocalDateTime;
//
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//public class ChatController {
//    private final ChatMessageRepository chatMessageRepository;
//    private final UserRepository userRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatService chatService;
//
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage chatMessage,
//                            SimpMessageHeaderAccessor headerAccessor) {
//        String senderUserName = ((String) headerAccessor.getSessionAttributes().get("username")).toLowerCase();
//
//        User sender = userRepository.findByUsername(senderUserName)
//                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderUserName));
//
//        String receiverUserName = chatMessage.getReceiver().trim().toLowerCase();
//        User receiver = userRepository.findByUsername(receiverUserName)
//                .filter(u -> u.getApprovalStatus() == ApprovalStatus.APPROVED)
//                .orElseThrow(() -> new RuntimeException("Recipient not available: " + receiverUserName));
//
//        LocalDateTime now = LocalDateTime.now();
//        chatMessage.setTimestamp(now);
//
//        // Save message with isRead=false
//        ChatMessageEntity entity = ChatMessageEntity.builder()
//                .sender(senderUserName)
//                .receiver(receiverUserName)
//                .content(chatMessage.getContent())
//                .type(chatMessage.getType())
//                .timestamp(now)
//                .isRead(false)
//                .build();
//        chatMessageRepository.save(entity);
//
//        // Send message to recipient with unread count
//        long unreadCount = chatService.getUnreadCount(senderUserName, receiverUserName);
//        chatMessage.setUnreadCount(unreadCount);
//        messagingTemplate.convertAndSendToUser(
//                receiverUserName,
//                "/queue/messages",
//                chatMessage
//        );
//
//        // Send delivery receipt to sender
//        ChatMessage receipt = new ChatMessage();
//        receipt.setType(MessageType.STATUS);
//        receipt.setContent("Delivered to " + receiverUserName);
//        receipt.setTimestamp(now);
//        messagingTemplate.convertAndSendToUser(
//                senderUserName,
//                "/queue/status",
//                receipt
//        );
//
//        log.info("Message sent from '{}' to '{}'", senderUserName, receiverUserName);
//    }
//
//    @MessageMapping("/chat.markAsRead")
//    public void markMessagesAsRead(@Payload ChatMessage chatMessage,
//                                   SimpMessageHeaderAccessor headerAccessor) {
//        String currentUser = ((String) headerAccessor.getSessionAttributes().get("username")).toLowerCase();
//        String sender = chatMessage.getSender().trim().toLowerCase();
//
//        chatService.markMessagesAsRead(sender, currentUser);
//
//        // Notify sender that messages were read
//        ChatMessage notification = new ChatMessage();
//        notification.setType(MessageType.STATUS);
//        notification.setContent("Messages read by " + currentUser);
//        notification.setTimestamp(LocalDateTime.now());
//        messagingTemplate.convertAndSendToUser(
//                sender,
//                "/queue/status",
//                notification
//        );
//    }
//
//    @MessageMapping("/chat.addUser")
//    public void addUser(@Payload ChatMessage chatMessage,
//                        SimpMessageHeaderAccessor headerAccessor) {
//        String username = chatMessage.getSender().trim().toLowerCase();
//        userRepository.findByUsername(username)
//                .ifPresent(user -> {
//                    headerAccessor.getSessionAttributes().put("username", username);
//                    headerAccessor.getSessionAttributes().put("userId", user.getId());
//                    log.info("User '{}' added to WebSocket session.", username);
//                });
//    }
//}