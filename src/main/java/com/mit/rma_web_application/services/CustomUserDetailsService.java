package com.mit.rma_web_application.services;

import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor-based dependency injection (Recommended)
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username for authentication
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user from the database using username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert Set<Role> to Collection<GrantedAuthority> (for roles)
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString())) // Add ROLE_ prefix to role names
                .collect(Collectors.toSet());

        // Return Spring Security's UserDetails with username, password, and authorities
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
    /**
     * Forgot Password Logic: Validate user and simulate sending a reset email.
     */
    public void handleForgotPassword(String username, String email) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty() || !userOpt.get().getEmail().equalsIgnoreCase(email)) {
            throw new Exception("Invalid username or email.");
        }

        // Simulate sending email (replace this with actual email logic if needed)
        System.out.println("Password reset link sent to: " + email);
    }

}
//package com.mit.rma_web_application.services;
//
//import com.mit.rma_web_application.models.User;
//import com.mit.rma_web_application.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    private final JavaMailSender mailSender;
//
//    @Autowired
//    public CustomUserDetailsService(UserRepository userRepository, JavaMailSender mailSender) {
//        this.userRepository = userRepository;
//        this.mailSender = mailSender;
//    }
//
//    /**
//     * Load user by username for authentication
//     */
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Retrieve user from the database using username
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//
//        // Convert Set<Role> to Collection<GrantedAuthority> (for roles)
//        Set<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString()))
//                .collect(Collectors.toSet());
//
//        // Return Spring Security's UserDetails
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                authorities
//        );
//    }
//
//    /**
//     * Forgot Password Logic: Validate user and send reset email.
//     */
//    public void handleForgotPassword(String username, String email) throws Exception {
//        Optional<User> userOpt = userRepository.findByUsername(username);
//
//        if (userOpt.isEmpty() || !userOpt.get().getEmail().equalsIgnoreCase(email)) {
//            throw new Exception("Invalid username or email.");
//        }
//
//        // Generate password reset token (for simplicity, use UUID)
//        String token = UUID.randomUUID().toString();
//
//        // Construct reset link (frontend must handle this route)
//        String resetLink = "http://localhost:5173/reset-password?token=" + token;
//
//        // Send the email
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Reset Request");
//        message.setText("Click this link to reset your password:\n" + resetLink);
//
//        mailSender.send(message);
//    }
//}
//
