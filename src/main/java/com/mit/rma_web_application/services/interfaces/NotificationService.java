//
//
//package com.mit.rma_web_application.services.interfaces;
//
//import com.mit.rma_web_application.models.Notification;
//
//import java.util.List;
//
//public interface NotificationService {
//    List<Notification> getUserNotifications(String username);
//    List<Notification> getRoleNotifications(String receiverRole);
//    List<Notification> getUnreadRoleNotifications(String receiverRole); // NEW
//    long countUnreadNotifications(String receiverRole);
//    Notification sendNotification(String receiverRole, String message, String type, String senderUsername, String status);
//    List<Notification> sendNotificationToRoles(List<String> receiverRoles, String message, String type, String senderUsername, String status);
//    Notification updateStatus(Long id, String status);
//    void deleteNotification(Long id);
//    Notification markAsRead(Long id);
//
//
//}
package com.mit.rma_web_application.services.interfaces;

import com.mit.rma_web_application.models.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> getUserNotifications(String username);
    List<Notification> getRoleNotifications(String receiverRole);
    List<Notification> getUnreadRoleNotifications(String receiverRole);
    long countUnreadNotifications(String receiverRole);
    Notification sendNotification(String receiverRole, String message, String type, String senderUsername, String status, Long requestsId);
    List<Notification> sendNotificationToRoles(List<String> receiverRoles, String message, String type, String senderUsername, String status);
    Notification updateStatus(Long id, String status);
    void deleteNotification(Long id);
    Notification markAsRead(Long id);
}