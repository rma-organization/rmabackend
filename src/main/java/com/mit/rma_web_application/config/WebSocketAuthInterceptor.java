////package com.mit.rma_web_application.config;
////
////import com.mit.rma_web_application.models.User;
////import com.mit.rma_web_application.repositories.UserRepository;
////import org.springframework.messaging.Message;
////import org.springframework.messaging.MessageChannel;
////import org.springframework.messaging.simp.stomp.StompCommand;
////import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
////import org.springframework.messaging.support.ChannelInterceptor;
////import org.springframework.messaging.support.MessageHeaderAccessor;
////import org.springframework.stereotype.Component;
////
////@Component
////public class WebSocketAuthInterceptor implements ChannelInterceptor {
////
////    private final UserRepository userRepository;
////
////    public WebSocketAuthInterceptor(UserRepository userRepository) {
////        this.userRepository = userRepository;
////    }
////
////    @Override
////    public Message<?> preSend(Message<?> message, MessageChannel channel) {
////        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
////
////        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
////            // Get user credentials from the headers
////            String email = accessor.getFirstNativeHeader("email");
////
////            if (email != null) {
////                // Verify user exists in DB
////                userRepository.findByEmail(email)
////                        .ifPresent(user -> {
////                            // Store user details in the session
////                            accessor.getSessionAttributes().put("userEmail", user.getEmail());
////                            accessor.getSessionAttributes().put("userId", user.getId());
////                        });
////            }
////        }
////
////        return message;
////    }
////}
//
//
//package com.mit.rma_web_application.config;
//
//import com.mit.rma_web_application.models.User;
//import com.mit.rma_web_application.repositories.UserRepository;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.stereotype.Component;
//
//@Component
//public class WebSocketAuthInterceptor implements ChannelInterceptor {
//
//    private final UserRepository userRepository;
//
//    public WebSocketAuthInterceptor(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//            // Get user credentials from the headers
//            String email = accessor.getFirstNativeHeader("email");
//
//            if (email != null) {
//                // Verify user exists in DB
//                userRepository.findByEmail(email)
//                        .ifPresent(user -> {
//                            // Store user details in the session
//                            accessor.getSessionAttributes().put("userEmail", user.getEmail());
//                            accessor.getSessionAttributes().put("userId", user.getId());
//
//                            // Set the user's email as the principal - THIS IS THE CRITICAL CHANGE
//                            accessor.setUser(() -> email);
//                        });
//            }
//        }
//
//        return message;
//    }
//}
package com.mit.rma_web_application.config;

import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            String email = accessor.getFirstNativeHeader("email");
            log.info("WebSocket connection attempt from email: {}", email);

            if (email != null) {
                userRepository.findByEmail(email)
                        .ifPresentOrElse(
                                user -> {
                                    if (user.getDeletedAt() != null) {
                                        log.error("Connection rejected - user is deleted: {}", email);
                                        throw new RuntimeException("User account is deleted");
                                    }
                                    if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
                                        log.error("Connection rejected - user not approved: {}", email);
                                        throw new RuntimeException("User account not approved");
                                    }

                                    accessor.setUser(() -> email); // Critical for @SendToUser
                                    accessor.getSessionAttributes().put("userEmail", user.getEmail());
                                    accessor.getSessionAttributes().put("userId", user.getId());
                                    log.info("WebSocket authenticated for user: {}", user.getEmail());
                                },
                                () -> {
                                    log.error("Connection rejected - user not found: {}", email);
                                    throw new RuntimeException("User not found");
                                }
                        );
            } else {
                log.error("Connection rejected - no email header provided");
                throw new RuntimeException("Email header is required");
            }
        }
        return message;
    }
}