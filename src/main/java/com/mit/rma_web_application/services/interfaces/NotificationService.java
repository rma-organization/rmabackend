package com.mit.rma_web_application.services.interfaces;

import com.mit.rma_web_application.models.Notification;
import java.util.List;

public interface NotificationService {
    void sendNotification(String recipientUsername, String message, String type);
    List<Notification> getUserNotifications(String username);
}