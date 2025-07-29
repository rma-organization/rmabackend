package com.mit.rma_web_application.config;

import com.mit.rma_web_application.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

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
            String token = null;

            // Extract JWT token from Authorization header
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                log.debug("🔐 Token extracted from Authorization header");
            }

            if (token == null) {
                log.error("❌ Missing JWT token in WebSocket CONNECT");
                throw new RuntimeException("Missing JWT token");
            }

            String username;
            try {
                username = jwtUtil.extractUsername(token);
                log.info("🔑 Extracted username: {}", username);
            } catch (ExpiredJwtException e) {
                log.error("❌ JWT token expired", e);
                throw new RuntimeException("JWT token expired");
            } catch (JwtException e) {
                log.error("❌ Invalid JWT token", e);
                throw new RuntimeException("Invalid JWT token");
            }

            if (!jwtUtil.validateToken(token, username)) {
                log.error("❌ JWT validation failed for user: {}", username);
                throw new RuntimeException("Invalid or expired JWT token");
            }

            // ✅ Use plain username as Principal
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.emptyList());

            accessor.setUser(authentication); // WebSocket principal = username
            log.info("✅ WebSocket authenticated for user: {}", username);
        }

        return message;
    }
}
