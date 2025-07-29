
package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.config.JwtUtil;
import com.mit.rma_web_application.dtos.*;
import com.mit.rma_web_application.models.*;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.CustomUserDetailsService;
import com.mit.rma_web_application.services.EmailService;
import com.mit.rma_web_application.services.interfaces.IUserService;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5174"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final IUserService userService;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        try {
            User newUser = userService.registerUser(registrationDto);
            if (newUser == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed.");
            }
            notificationService.sendNotification(
                    "ADMIN",
                    "New user registered: " + newUser.getUsername() + ". Please review and approve.",
                    "INFO",
                    newUser.getUsername(),
                    null,
                    null
            );

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully. Awaiting admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = userService.findByUsername(authRequest.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not approved by admin.");
            }

            Role inputRole;
            try {
                inputRole = Role.valueOf(authRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid role selected");
            }

            if (!user.getRoles().contains(inputRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have the requested role.");
            }

            // ✅ Use List.of(...) to fix the error
            String token = jwtUtil.generateToken(user.getUsername(), List.of(inputRole.name()));

            return ResponseEntity.ok(new AuthResponse(token, inputRole.name()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !userOpt.get().getEmail().equalsIgnoreCase(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or email."));
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String subject = "RMA Web Application - Password Reset Request";
        String text = "Dear " + user.getUsername() + ",\n\n" +
                "We received a request to reset your password. Use the following link to reset your password. This link expires in 30 minutes:\n\n" +
                resetLink + "\n\nIf you did not request this, ignore this email.";

        try {
            emailService.sendSimpleMessage(user.getEmail(), subject, text);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Password reset link sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByResetToken(request.getToken());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token."));
        }

        User user = userOpt.get();
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token has expired."));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching users");
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO dto) {
        Optional<User> userOptional = userRepository.findById(dto.getId());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        try {
            Set<Role> roleSet = dto.getRoles().stream()
                    .map(roleStr -> Role.valueOf(roleStr.toUpperCase()))
                    .collect(Collectors.toSet());

            user.setRoles(roleSet);
            user.setApprovalStatus(dto.getApprovalStatus());

            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role provided: " + e.getMessage());
        }
    }

    @GetMapping("/pending-users")
    public ResponseEntity<?> getPendingUsers() {
        try {
            List<User> pendingUsers = userService.getPendingUsers();
            return ResponseEntity.ok(pendingUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch pending users.");
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveUser(@RequestBody ApprovelStatusDTO dto) {
        Optional<User> userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        try {
            ApprovalStatus status = dto.getApprovalStatus();
            user.setApprovalStatus(status);

            if (status == ApprovalStatus.APPROVED) {
                user.setApprovedAt(LocalDateTime.now());
            }

            userRepository.save(user);
            return ResponseEntity.ok("User approval status updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid approvalStatus");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, @RequestParam String username) {
        boolean isValid = jwtUtil.validateToken(token, username);
        return isValid ? ResponseEntity.ok("Valid Token") : ResponseEntity.status(401).body("Invalid Token");
    }

    @GetMapping("/admin/user-stats")
    public ResponseEntity<?> getUserStats() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }
}

