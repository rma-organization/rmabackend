

package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.models.Notification;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get notifications by role
    @GetMapping("/role")
    public ResponseEntity<List<Notification>> getRoleNotifications(@RequestHeader("Role") String role) {
        return ResponseEntity.ok(notificationService.getRoleNotifications(role));
    }

    // Get notifications for logged-in user
    @GetMapping("/user")
    public ResponseEntity<List<Notification>> getUserNotifications(Principal principal) {
        return ResponseEntity.ok(notificationService.getUserNotifications(principal.getName()));
    }

    // Send notification to single role
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestParam String receiverRole,
                                                         @RequestParam String message,
                                                         @RequestParam String type,
                                                         @RequestParam(required = false) String status,
                                                         Principal principal) {
        String senderUsername = principal.getName();
        Notification notification = notificationService.sendNotification(receiverRole, message, type, senderUsername, status);
        return ResponseEntity.ok(notification);
    }

    // Send notification to multiple roles at once
    @PostMapping("/send-multiple")
    public ResponseEntity<List<Notification>> sendNotificationToMultipleRoles(@RequestParam List<String> receiverRoles,
                                                                              @RequestParam String message,
                                                                              @RequestParam String type,
                                                                              @RequestParam(required = false) String status,
                                                                              Principal principal) {
        String senderUsername = principal.getName();
        List<Notification> notifications = notificationService.sendNotificationToRoles(receiverRoles, message, type, senderUsername, status);
        return ResponseEntity.ok(notifications);
    }

    // Update notification status by ID
    @PutMapping("/{id}/status")
    public ResponseEntity<Notification> updateNotificationStatus(@PathVariable Long id,
                                                                 @RequestParam String status) {
        Notification updated = notificationService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // NotificationController.java

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestHeader("Role") String role) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(role));
    }

    // Get only unread notifications by role
    @GetMapping("/role/unread")
    public ResponseEntity<List<Notification>> getUnreadRoleNotifications(@RequestHeader("Role") String role) {
        return ResponseEntity.ok(notificationService.getUnreadRoleNotifications(role));
    }


    @PutMapping("/{id}/mark-read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long id) {
        Notification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(updated);
    }




}

