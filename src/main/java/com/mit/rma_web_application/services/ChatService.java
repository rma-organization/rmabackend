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
}
