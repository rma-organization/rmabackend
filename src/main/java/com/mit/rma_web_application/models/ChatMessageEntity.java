// package: com.mit.rma_web_application.models

package com.mit.rma_web_application.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String sender;

    private String receiver; // ✅ New field

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private LocalDateTime timestamp;
}
