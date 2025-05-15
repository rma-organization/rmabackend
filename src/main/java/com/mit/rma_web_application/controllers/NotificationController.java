package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.models.NotificationMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping
    public ResponseEntity<List<NotificationMessage>> getNotifications(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "") String page
    ) {
        // Get roles from authenticated user
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Dummy data — you can later load this from a DB or cache if needed
        List<NotificationMessage> notifications = List.of(
                new NotificationMessage("Welcome to the dashboard!", "info"),
                new NotificationMessage("Your profile was updated.", "success")
        );

        // You can filter based on roles or page if needed
        return ResponseEntity.ok(notifications);
    }
}
