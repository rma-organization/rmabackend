
package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.config.JwtUtil;
import com.mit.rma_web_application.dtos.*;
import com.mit.rma_web_application.models.*;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.CustomUserDetailsService;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    /**
     * Endpoint for user registration.
     */
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

    /**
     * Endpoint for user login with role validation and admin approval check.
     */
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
        String token = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, authRequest.getRole()));
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();  // Fetch all users, regardless of their approval status.
        return ResponseEntity.ok(allUsers);
    }

    /**
     * Endpoint to get all users with pending approval.
     */
    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pendingUsers = userRepository.findByApprovalStatus(ApprovalStatus.PENDING);
        return ResponseEntity.ok(pendingUsers);
    }

    /**
     * Endpoint for approving a user.
     * Only an admin should be allowed to approve a user.
     */
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
        userRepository.save(user);
        return ResponseEntity.ok("User approval status updated successfully.");
    }

    /**
     * Endpoint to validate a JWT token.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, @RequestParam String username) {
        boolean isValid = jwtUtil.validateToken(token, username);
        return isValid ? ResponseEntity.ok("Valid Token") : ResponseEntity.status(401).body("Invalid Token");
    }
}
