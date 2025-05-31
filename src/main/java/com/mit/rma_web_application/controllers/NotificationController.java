//package com.mit.rma_web_application.controllers;
//
//import com.mit.rma_web_application.models.Notification;
//import com.mit.rma_web_application.services.interfaces.NotificationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/notifications")
//public class NotificationController {
//
//    @Autowired
//    private NotificationService notificationService;
//
//    // Get notifications for the currently logged-in user (based on JWT principal)
//    @GetMapping
//    public ResponseEntity<List<Notification>> getUserNotifications(Principal principal) {
//        String username = principal.getName();
//        List<Notification> notifications = notificationService.getUserNotifications(username);
//        return ResponseEntity.ok(notifications);
//    }
//}
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

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(notificationService.getUserNotifications(username));
    }
}
