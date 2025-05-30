package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.models.Notification;
import com.mit.rma_web_application.repositories.NotificationRepository;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotification(String recipientUsername, String message, String type) {
        Notification notification = new Notification();
        notification.setRecipientUsername(recipientUsername);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(
                recipientUsername,
                "/queue/notifications",
                notification
        );
    }

    @Override
    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByRecipientUsername(username);
    }
}


