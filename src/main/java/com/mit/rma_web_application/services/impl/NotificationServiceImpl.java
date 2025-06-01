//package com.mit.rma_web_application.services.impl;
//
//import com.mit.rma_web_application.models.Notification;
//import com.mit.rma_web_application.repositories.NotificationRepository;
//import com.mit.rma_web_application.services.interfaces.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class NotificationServiceImpl implements NotificationService {
//
//    @Autowired
//    private NotificationRepository notificationRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Override
//    public void sendNotification(String recipientUsername, String message, String type) {
//        Notification notification = new Notification();
//        notification.setRecipientUsername(recipientUsername);
//        notification.setMessage(message);
//        notification.setType(type);
//        notification.setRead(false);
//        notificationRepository.save(notification);
//
//        messagingTemplate.convertAndSendToUser(
//                recipientUsername,
//                "/queue/notifications",
//                notification
//        );
//    }
//
//    @Override
//    public List<Notification> getUserNotifications(String username) {
//        return notificationRepository.findByRecipientUsername(username);
//    }
//}
//
//
//package com.mit.rma_web_application.services.impl;
//
//import com.mit.rma_web_application.models.Notification;
//import com.mit.rma_web_application.repositories.NotificationRepository;
//import com.mit.rma_web_application.services.interfaces.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class NotificationServiceImpl implements NotificationService {
//
//    @Autowired
//    private NotificationRepository notificationRepository;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @Override
//    public void sendNotification(String recipientUsername, String message, String type) {
//        Notification notification = new Notification();
//        notification.setRecipientUsername(recipientUsername);
//        notification.setMessage(message);
//        notification.setType(type);
//        notification.setRead(false);
//
//        // ✅ Save and flush to ensure ID is set
//        Notification saved = notificationRepository.save(notification);
//
//        // 🧪 Optional: Log to verify ID is present
//        System.out.println("📤 Sending notification to " + recipientUsername + ": ID=" + saved.getId());
//
//        // ✅ Send saved object with ID
//        messagingTemplate.convertAndSendToUser(
//                recipientUsername,
//                "/queue/notifications",
//                saved
//        );
//    }
//
//    @Override
//    public List<Notification> getUserNotifications(String username) {
//        return notificationRepository.findByRecipientUsername(username);
//    }
//}
//package com.mit.rma_web_application.services;
//
//import com.mit.rma_web_application.models.Notification;
//import com.mit.rma_web_application.repositories.NotificationRepository;
//import com.mit.rma_web_application.services.interfaces.NotificationService;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class NotificationServiceImpl implements NotificationService {
//
//    private final NotificationRepository notificationRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//    private final UserRepository userRepository;
//
//    public NotificationServiceImpl(
//            NotificationRepository notificationRepository,
//            SimpMessagingTemplate messagingTemplate,
//            UserRepository userRepository
//    ) {
//        this.notificationRepository = notificationRepository;
//        this.messagingTemplate = messagingTemplate;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public List<Notification> getUserNotifications(String username) {
//        return notificationRepository.findByRecipientUsernameOrderByTimestampDesc(username);
//    }
//
//    @Override
//    public Notification sendNotification(String recipientUsername, String message, String type) {
//        Notification notification = new Notification();
//        notification.setRecipientUsername(recipientUsername);
//        notification.setMessage(message);
//        notification.setType(type);
//        notification.setRead(false);
//        notification.setTimestamp(LocalDateTime.now());
//
//        Notification saved = notificationRepository.save(notification);
//        messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/notifications", saved);
//        return saved;
//    }
//
//    @Override
//    public void sendNotificationToRole(String roleName, String message, String type) {
//        List<User> usersWithRole = userRepository.findUsersByRole(roleName);
//        for (User user : usersWithRole) {
//            sendNotification(user.getUsername(), message, type);
//        }
//    }
//}
//
package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.models.Notification;
import com.mit.rma_web_application.repositories.NotificationRepository;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return notificationRepository.findByRecipientUsernameOrderByTimestampDesc(username);
    }

    @Override
    public List<Notification> getRoleNotifications(String role) {
        return notificationRepository.findByRecipientUsernameOrderByTimestampDesc(role);
    }

    @Override
    public Notification sendNotification(String recipientUsername, String message, String type) {
        Notification notification = new Notification();
        notification.setRecipientUsername(recipientUsername); // Can be role now
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        // Role-based destination
        messagingTemplate.convertAndSend("/topic/notifications/" + recipientUsername.toLowerCase(), saved);
        return saved;
    }
}


