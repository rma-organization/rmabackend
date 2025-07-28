package com.mit.rma_web_application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String type;
    private String message;
    private String receiverRole;
    private String status;
    private String senderUsername;
    private Long requestsId;
}
