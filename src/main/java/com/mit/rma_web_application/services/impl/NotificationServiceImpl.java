package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.models.NotificationMessage;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    /**
     * Sends a notification to all users who have any of the specified roles.
     * @param roleNames List of role names (e.g., "ADMIN", "ENGINEER")
     * @param message The notification message to send
     */
    @Override
    public void notifyRoles(List<String> roleNames, String message) {
        List<User> allUsers = userRepository.findAll();

        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> roleNames.contains(role.name())))
                .collect(Collectors.toList());

        for (User user : filteredUsers) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/notifications",
                    new NotificationMessage(message, "role-notification")
            );
        }
    }

    /**
     * Sends a notification to a specific user by username.
     * @param username The recipient's username
     * @param message The notification message
     */
    @Override
    public void notifyUser(String username, String message) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                new NotificationMessage(message, "user-notification")
        );
    }
}
