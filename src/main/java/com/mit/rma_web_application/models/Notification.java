//package com.mit.rma_web_application.models;
//
//import jakarta.persistence.*;
//
//@Entity
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String recipientUsername;
//    private String message;
//    private String type;
//    private boolean isRead;
//
//    // Getters and setters
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getRecipientUsername() {
//        return recipientUsername;
//    }
//
//    public void setRecipientUsername(String recipientUsername) {
//        this.recipientUsername = recipientUsername;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public boolean isRead() {
//        return isRead;
//    }
//
//    public void setRead(boolean read) {
//        isRead = read;
//    }
//}
package com.mit.rma_web_application.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientUsername;
    private String message;
    private String type;
    private boolean isRead;

    private LocalDateTime timestamp;

    // Getters and setters
    public Long getId() { return id; }

    public String getRecipientUsername() { return recipientUsername; }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }

    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
