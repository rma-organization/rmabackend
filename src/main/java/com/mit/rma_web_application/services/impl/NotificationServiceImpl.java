package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.models.Notification;
import com.mit.rma_web_application.models.Request;
import com.mit.rma_web_application.repositories.NotificationRepository;
import com.mit.rma_web_application.repositories.RequestRepository;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RequestRepository requestRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   SimpMessagingTemplate messagingTemplate,
                                   RequestRepository requestRepository) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.requestRepository = requestRepository;
    }

    // ==============================
    // READ METHODS
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getRoleNotifications(String receiverRole) {
        return notificationRepository.findByReceiverRoleOrderByTimestampDesc(receiverRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getEngineerNotifications(String engineerUsername) {
        return notificationRepository.findByReceiverRoleAndUserNameOrderByTimestampDesc("engineer", engineerUsername);
    }

    // FIXED to use join query for engineer user notifications
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(String userName) {
        return notificationRepository.findEngineerNotificationsForUser(userName);
    }

    // ==============================
    // SEND NOTIFICATION
    // ==============================

    @Override
    @Transactional
    public Notification sendNotification(String receiverRole,
                                         String message,
                                         String type,
                                         String senderUsername,
                                         String status,
                                         Long requestId) {

        Notification notification = new Notification();
        notification.setReceiverRole(receiverRole);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        notification.setStatus(status);
        notification.setRequestsId(requestId);
        notification.setUserName(null); // will set below

        String receiverUsername = null;

        if ("engineer".equalsIgnoreCase(receiverRole) && requestId != null) {
            Optional<Request> optionalRequest = requestRepository.findById(requestId);
            if (optionalRequest.isPresent()) {
                receiverUsername = optionalRequest.get().getRequestedBy();
                notification.setUserName(receiverUsername); // store actual engineer username
            } else {
                log.warn("⚠️ Request not found for ID {}, cannot determine engineer username", requestId);
            }
        } else {
            // For other roles, store role as userName
            notification.setUserName(receiverRole);
        }

        Notification saved = notificationRepository.save(notification);

        try {
            if ("engineer".equalsIgnoreCase(receiverRole)) {
                if (receiverUsername != null && !receiverUsername.trim().isEmpty()) {
                    messagingTemplate.convertAndSendToUser(receiverUsername, "/queue/notifications", saved);
                    log.info("✅ Sent private notification to engineer user: {}", receiverUsername);
                }
                messagingTemplate.convertAndSend("/topic/notifications/engineer", saved);
                log.info("📢 Broadcast to /topic/notifications/engineer");
            } else {
                messagingTemplate.convertAndSend("/topic/notifications/" + receiverRole.toLowerCase(), saved);
                log.info("📢 Sent to /topic/notifications/{}", receiverRole.toLowerCase());
            }
        } catch (Exception e) {
            log.error("❌ Error sending WebSocket notification", e);
            throw e;
        }

        return saved;
    }

    @Override
    @Transactional
    public List<Notification> sendNotificationToRoles(List<String> receiverRoles,
                                                      String message,
                                                      String type,
                                                      String senderUsername,
                                                      String status) {
        List<Notification> notifications = new ArrayList<>();
        for (String role : receiverRoles) {
            Notification notification = new Notification();
            notification.setReceiverRole(role);
            notification.setMessage(message);
            notification.setType(type);
            notification.setUserName(role);
            notification.setRead(false);
            notification.setTimestamp(LocalDateTime.now());
            notification.setStatus(status);

            Notification saved = notificationRepository.save(notification);
            messagingTemplate.convertAndSend("/topic/notifications/" + role.toLowerCase(), saved);
            log.info("✅ Sent notification to /topic/notifications/{}", role.toLowerCase());

            notifications.add(saved);
        }
        return notifications;
    }

    // ==============================
    // UPDATE & DELETE
    // ==============================

    @Override
    @Transactional
    public Notification updateStatus(Long id, String status) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setStatus(status);
        Notification updated = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getReceiverRole().toLowerCase(), updated);
        log.info("🔄 Updated and resent notification ID {} with new status: {}", id, status);

        return updated;
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
        log.info("🗑️ Deleted notification with ID: {}", id);
    }

    // ==============================
    // UNREAD & READ
    // ==============================

    @Override
    @Transactional(readOnly = true)
    public long countUnreadNotifications(String receiverRole) {
        return notificationRepository.countByReceiverRoleAndReadIsFalse(receiverRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadRoleNotifications(String receiverRole) {
        return notificationRepository.findByReceiverRoleAndReadFalseOrderByTimestampDesc(receiverRole);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        log.info("✅ Marked notification as read [ID: {}]", id);
        return updated;
    }
}
