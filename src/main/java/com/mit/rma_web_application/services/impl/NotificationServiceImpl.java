package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.models.Notification;
import com.mit.rma_web_application.repositories.NotificationRepository;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByReceiverRoleOrderByTimestampDesc(username);
    }

    @Override
    public List<Notification> getRoleNotifications(String receiverRole) {
        return notificationRepository.findByReceiverRoleOrderByTimestampDesc(receiverRole);
    }

    @Override
    public Notification sendNotification(String receiverRole, String message, String type, String senderUsername, String status) {
        Notification notification = new Notification();
        notification.setReceiverRole(receiverRole);
        notification.setMessage(message);
        notification.setType(type);
        notification.setUserName(senderUsername);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        notification.setStatus(status);

        Notification saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + receiverRole.toLowerCase(), saved);
        return saved;
    }

    @Override
    public List<Notification> sendNotificationToRoles(List<String> receiverRoles, String message, String type, String senderUsername, String status) {
        List<Notification> notifications = new ArrayList<>();

        for (String role : receiverRoles) {
            Notification notification = new Notification();
            notification.setReceiverRole(role);
            notification.setMessage(message);
            notification.setType(type);
            notification.setUserName(senderUsername);
            notification.setRead(false);
            notification.setTimestamp(LocalDateTime.now());
            notification.setStatus(status);

            Notification saved = notificationRepository.save(notification);
            messagingTemplate.convertAndSend("/topic/notifications/" + role.toLowerCase(), saved);
            notifications.add(saved);
        }

        return notifications;
    }

    @Override
    public Notification updateStatus(Long id, String status) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setStatus(status);
        Notification updated = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getReceiverRole().toLowerCase(), updated);
        return updated;
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    public long countUnreadNotifications(String receiverRole) {
        return notificationRepository.countByReceiverRoleAndReadIsFalse(receiverRole);
    }

    @Override
    public List<Notification> getUnreadRoleNotifications(String receiverRole) {
        return notificationRepository.findByReceiverRoleAndReadFalseOrderByTimestampDesc(receiverRole);
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));

        notification.setRead(true);  // Correct setter here
        return notificationRepository.save(notification);
    }
}
