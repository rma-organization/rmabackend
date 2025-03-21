package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
}