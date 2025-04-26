//// package: com.mit.rma_web_application.dtos
//
//package com.mit.rma_web_application.dtos;
//
//import com.mit.rma_web_application.models.MessageType;
//import lombok.*;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ChatMessage {
//    private String content;
//    private String sender;
//    private String receiver; // ✅ New field
//    private MessageType type;
//}

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
    private LocalDateTime timestamp; // Add this field

}
