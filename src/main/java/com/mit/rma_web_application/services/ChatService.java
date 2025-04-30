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
                .receiver(chatMessage.getReceiver()) // Make sure to set the receiver field
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageRepository.save(entity);
        return chatMessage;
    }

    public List<ChatMessageEntity> getAllMessages() {
        return chatMessageRepository.findAll();
    }

    /**
     * Gets all messages exchanged between two users in both directions
     * @param sender the first user
     * @param receiver the second user
     * @return List of messages exchanged between the users
     */
    public List<ChatMessageEntity> getMessagesBetweenUsers(String sender, String receiver) {
        // Find messages where user1 is sender and user2 is receiver OR user2 is sender and user1 is receiver
        return chatMessageRepository.findMessagesBetweenUsers(sender, receiver);
    }
}