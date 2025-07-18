package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor-based dependency injection
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with roles.
     */
    @Override
    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        // Check if the username is already taken
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken.");
        }

        // Check if the email is already taken
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        // Encode the password securely
        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        // Create and save the new user
        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(encodedPassword);
        user.setRoles(registerRequestDTO.getRoles());

        return userRepository.save(user);
    }

    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", userRepository.countAllUsers());

        // Users by role
        List<Object[]> usersByRole = userRepository.countUsersByRole();
        java.util.Map<String, Long> roleCounts = new java.util.HashMap<>();
        for (Object[] row : usersByRole) {
            // row[0] is a Set<Role>, row[1] is count
            @SuppressWarnings("unchecked")
            java.util.Set<com.mit.rma_web_application.models.Role> roles = (java.util.Set<com.mit.rma_web_application.models.Role>) row[0];
            Long count = (Long) row[1];
            for (com.mit.rma_web_application.models.Role role : roles) {
                roleCounts.put(role.name(), roleCounts.getOrDefault(role.name(), 0L) + count);
            }
        }
        stats.put("usersByRole", roleCounts);

        // Pending approvals
        stats.put("pendingApprovals", userRepository.countPendingApprovals());

        // Recently registered users (last 7 days)
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);
        stats.put("recentUsers", userRepository.findRecentUsers(sevenDaysAgo));

        return stats;
    }
}