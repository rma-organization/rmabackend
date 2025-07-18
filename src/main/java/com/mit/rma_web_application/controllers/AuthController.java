

package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.AuthRequest;
import com.mit.rma_web_application.dtos.AuthResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.models.Role;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.services.UserService;
import com.mit.rma_web_application.services.interfaces.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://localhost:5174"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registrationDto) {
        if (userService.existsByUsername(registrationDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        User newUser = userService.registerUser(registrationDto);

        notificationService.sendNotification(
                "ADMIN",
                "New user registered: " + newUser.getUsername() + ". Please review and approve.",
                "INFO",
                newUser.getUsername(),
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful. Await admin approval.");
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not approved yet");
            }

            Role inputRole;
            try {
                inputRole = Role.valueOf(authRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid role selected");
            }

            Set<Role> roles = user.getRoles();
            if (!roles.contains(inputRole)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid role selected");
            }

            String token = userService.generateToken(user.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, authRequest.getRole()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
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
    public ResponseEntity<?> approveUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String approvalStatus = request.get("approvalStatus");

        if (username == null || approvalStatus == null) {
            return ResponseEntity.badRequest().body("Missing username or approvalStatus");
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            user.setApprovalStatus(ApprovalStatus.valueOf(approvalStatus));
            userService.save(user);
            return ResponseEntity.ok("User status updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid approvalStatus");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching users");
        }
    }
}