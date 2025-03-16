package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.RegisterRequest;
import com.mit.rma_web_application.model.Role;
import com.mit.rma_web_application.model.RoleName;
import com.mit.rma_web_application.model.User;
import com.mit.rma_web_application.repositories.RoleRepository;
import com.mit.rma_web_application.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor-based dependency injection
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with roles
     */
    public User registerUser(RegisterRequest registerRequest) {
        // Check if the username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken.");
        }

        // Check if the email is already taken
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        // Encode password securely
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Fetch roles from DB and validate
        Set<Role> roles = registerRequest.getRoles().stream()
                .map(roleNameStr -> {
                    RoleName roleEnum;
                    try {
                        roleEnum = RoleName.valueOf(roleNameStr.toUpperCase()); // Convert String to Enum
                    } catch (IllegalArgumentException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleNameStr);
                    }
                    return roleRepository.findByName(roleEnum)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleEnum));
                })
                .collect(Collectors.toSet());

        // Create and save the new user
        User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), encodedPassword, roles);
        return userRepository.save(user);
    }
}
