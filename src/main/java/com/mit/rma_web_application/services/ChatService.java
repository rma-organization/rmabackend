
// ChatService.java
package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.ChatMessage;
import com.mit.rma_web_application.models.ChatMessageEntity;
import com.mit.rma_web_application.models.MessageType;
import com.mit.rma_web_application.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .sender(chatMessage.getSender())
                .receiver(chatMessage.getReceiver())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .timestamp(LocalDateTime.now())
                .isRead(false) // New messages are unread by default
                .build();

        chatMessageRepository.save(entity);
        return chatMessage;
    }

    public List<ChatMessageEntity> getAllMessages() {
        return chatMessageRepository.findAll();
    }

    public List<ChatMessageEntity> getMessagesBetweenUsers(String sender, String receiver) {
        return chatMessageRepository.findMessagesBetweenUsers(sender, receiver);
    }

    public long getUnreadCount(String sender, String receiver) {
        return chatMessageRepository.countUnreadMessages(sender, receiver);
    }

    public void markMessagesAsRead(String sender, String receiver) {
        chatMessageRepository.markMessagesAsRead(sender, receiver);
    }

}