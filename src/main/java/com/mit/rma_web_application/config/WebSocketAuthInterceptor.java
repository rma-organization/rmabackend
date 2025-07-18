

package com.mit.rma_web_application.config;

import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Missing or invalid Authorization header in WebSocket CONNECT");
                throw new RuntimeException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            String username;
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                log.error("Invalid JWT token in WebSocket CONNECT: {}", e.getMessage());
                throw new RuntimeException("Invalid JWT token");
            }

            if (!jwtUtil.validateToken(token, username)) {
                log.error("JWT validation failed for user: {}", username);
                throw new RuntimeException("Invalid or expired JWT token");
            }

            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isEmpty()) {
                log.error("User not found in database: {}", username);
                throw new RuntimeException("User not found");
            }

            User user = optionalUser.get();

            // You can optionally extract roles from the token if needed
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user, null, List.of() // or use roles
            );

            accessor.setUser(authToken);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("WebSocket connected successfully for user: {}", username);
        }

        return message;
    }
}
