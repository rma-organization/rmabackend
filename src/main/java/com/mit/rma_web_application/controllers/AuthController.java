//package com.mit.rma_web_application.controllers;
//
//import com.mit.rma_web_application.dtos.AuthRequest;
//import com.mit.rma_web_application.dtos.AuthResponse;
//import com.mit.rma_web_application.dtos.RegisterRequest;
//import com.mit.rma_web_application.model.Role;
//import com.mit.rma_web_application.model.User;
//import com.mit.rma_web_application.repositories.UserRepository;
//import com.mit.rma_web_application.config.JwtUtil;
//import com.mit.rma_web_application.services.CustomUserDetailsService;
//import com.mit.rma_web_application.services.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private CustomUserDetailsService userDetailsService;
//
//    /**
//     * Endpoint for user registration.
//     */
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
//        // Check if the username is already taken
//        if (userRepository.existsByUsername(registerRequest.getUsername())) {
//            return ResponseEntity.badRequest().body("Username is already taken.");
//        }
//
//        // Check if the email is already in use
//        if (userRepository.existsByEmail(registerRequest.getEmail())) {
//            return ResponseEntity.badRequest().body("Email is already in use.");
//        }
//
//        // Register the user (assuming the service handles role assignment and password encoding)
//        try {
//            User registeredUser = userService.registerUser(registerRequest);
//            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Endpoint for user login with role validation and admin approval check.
//     */
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
//        Optional<User> userOptional = userRepository.findByUsername(authRequest.getUsername());
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//        User user = userOptional.get();
//
//        // Check if the user is approved by the admin
//        if (!user.isApproved()) {
//            return ResponseEntity.status(403).body("User is not approved by admin.");
//        }
//
//        // Check if the selected role is valid
////        boolean isValidRole = user.getRoles().stream()
////                .anyMatch(role -> role.getName().name().equalsIgnoreCase(authRequest.getRole())); // Ensure role comparison is correct
////
////        if (!isValidRole) {
////            return ResponseEntity.status(403).body("Invalid role selection.");
////        }
//
//        // Authenticate the user
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Invalid credentials");
//        }
//
//        // Generate JWT token
//        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
//        String token = jwtUtil.generateToken(userDetails.getUsername());
//
//        // Return the token and selected role in the response
//        return ResponseEntity.ok(new AuthResponse(token, authRequest.getRole()));
//    }
//
//    /**
//     * Endpoint to validate a JWT token.
//     */
//    @GetMapping("/validate")
//    public ResponseEntity<?> validateToken(@RequestParam String token, @RequestParam String username) {
//        boolean isValid = jwtUtil.validateToken(token, username);
//
//        if (isValid) {
//            return ResponseEntity.ok("Valid Token");
//        } else {
//            return ResponseEntity.status(401).body("Invalid Token");
//        }
//    }
//}
package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.AuthRequest;
import com.mit.rma_web_application.dtos.AuthResponse;
import com.mit.rma_web_application.dtos.RegisterRequest;
import com.mit.rma_web_application.model.User;
import com.mit.rma_web_application.model.ApprovalStatus;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.config.JwtUtil;
import com.mit.rma_web_application.services.CustomUserDetailsService;
import com.mit.rma_web_application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Endpoint for user registration.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Check if the username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }

        // Check if the email is already in use
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use.");
        }

        // Register the user (assuming the service handles role assignment and password encoding)
        try {
            User registeredUser = userService.registerUser(registerRequest);
            return ResponseEntity.ok("User registered successfully. Awaiting admin approval.");
        } catch (IllegalArgumentException e) {
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

        // Check if the user is approved by the admin
        if (!user.isApproved()) {
            return ResponseEntity.status(403).body("User is not approved by admin.");
        }

        // Authenticate the user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // Return the token and selected role in the response
        return ResponseEntity.ok(new AuthResponse(token, authRequest.getRole()));
    }

    /**
     * Endpoint for approving a user via query parameter.
     * Only an admin should be allowed to approve a user.
     */
    @PostMapping("/approve")
    public ResponseEntity<?> approveUser(@RequestParam String username) {
        // Fetch the user by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        // Check if the user is already approved
        if (user.isApproved()) {
            return ResponseEntity.status(400).body("User is already approved.");
        }

        // Update the approval status of the user
        user.setApprovalStatus(ApprovalStatus.APPROVED);  // Use the setApprovalStatus method

        // Save the updated user back to the database
        userRepository.save(user);

        return ResponseEntity.ok("User approved successfully.");
    }

    /**
     * Endpoint to validate a JWT token.
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, @RequestParam String username) {
        boolean isValid = jwtUtil.validateToken(token, username);

        if (isValid) {
            return ResponseEntity.ok("Valid Token");
        } else {
            return ResponseEntity.status(401).body("Invalid Token");
        }
    }
}
