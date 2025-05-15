package com.mit.rma_web_application.models;

public class NotificationMessage {

    private String message;
    private String notificationType;

    public NotificationMessage(String message, String notificationType) {
        this.message = message;
        this.notificationType = notificationType;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
