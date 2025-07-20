

package com.mit.rma_web_application.dtos;

import com.mit.rma_web_application.models.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private String receiver;
    private MessageType type;
    private LocalDateTime timestamp;
    private long unreadCount; // Added this field
}
