package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.config.JwtUtil;
import com.mit.rma_web_application.dtos.*;
import com.mit.rma_web_application.models.*;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.CustomUserDetailsService;
import com.mit.rma_web_application.services.EmailService;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }
        try {
            userService.registerUser(registerRequest);
            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        if (!ApprovalStatus.APPROVED.equals(user.getApprovalStatus())) {
            return ResponseEntity.status(403).body("User is not approved by admin.");
        }

        if (!user.getRoles().contains(authRequest.getRole())) {
            return ResponseEntity.status(403).body("User does not have the requested role.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername(), authRequest.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, authRequest.getRole()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return ResponseEntity.ok(allUsers);
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
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pendingUsers = userRepository.findByApprovalStatus(ApprovalStatus.PENDING);
        return ResponseEntity.ok(pendingUsers);
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveUser(@RequestBody ApprovelStatusDTO approvelStatusDTO) {
        Optional<User> userOptional = userRepository.findByUsername(approvelStatusDTO.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();
        if (ApprovalStatus.APPROVED.equals(user.getApprovalStatus())) {
            return ResponseEntity.status(400).body("User is already approved.");
        }

        user.setApprovalStatus(approvelStatusDTO.getApprovalStatus());
        if (ApprovalStatus.APPROVED.equals(approvelStatusDTO.getApprovalStatus())) {
            user.setApprovedAt(LocalDateTime.now());
        }

        userRepository.save(user);
        return ResponseEntity.ok("User approval status updated successfully.");
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, @RequestParam String username) {
        boolean isValid = jwtUtil.validateToken(token, username);
        return isValid ? ResponseEntity.ok("Valid Token") : ResponseEntity.status(401).body("Invalid Token");
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
                "We received a request to reset your password. Please use the following link to reset your password. This link will expire in 30 minutes.\n\n" +
                resetLink + "\n\nIf you did not request a password reset, please ignore this email.";
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

    @GetMapping("/admin/user-stats")
    public ResponseEntity<?> getUserStats() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }
}
