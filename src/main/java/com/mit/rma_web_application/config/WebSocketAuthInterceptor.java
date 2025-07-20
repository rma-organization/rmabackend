package com.mit.rma_web_application.config;

import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Now using the 'username' header instead of 'email'
            String username = accessor.getFirstNativeHeader("username");
            log.info("WebSocket connection attempt from username: {}", username);

            if (username != null) {
                userRepository.findByUsername(username)
                        .ifPresentOrElse(
                                user -> {
                                    if (user.getDeletedAt() != null) {
                                        log.error("Connection rejected - user is deleted: {}", username);
                                        throw new RuntimeException("User account is deleted");
                                    }
                                    if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
                                        log.error("Connection rejected - user not approved: {}", username);
                                        throw new RuntimeException("User account not approved");
                                    }

                                    accessor.setUser(() -> username); // Set principal with the username
                                    accessor.getSessionAttributes().put("userEmail", user.getEmail());
                                    accessor.getSessionAttributes().put("userId", user.getId());
                                    log.info("WebSocket authenticated for user: {}", user.getUsername());
                                },
                                () -> {
                                    log.error("Connection rejected - user not found: {}", username);
                                    throw new RuntimeException("User not found");
                                }
                        );
            } else {
                log.error("Connection rejected - no username header provided");
                throw new RuntimeException("Username header is required");
            }
        }
        return message;
    }
}
