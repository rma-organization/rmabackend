
package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.config.JwtUtil;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.ApprovalStatus;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User registerUser(RegisterRequestDTO registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRoles(registrationDto.getRoles());
        user.setApprovalStatus(ApprovalStatus.PENDING);
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Override
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApprovalStatus(ApprovalStatus.APPROVED);
        return userRepository.save(user);
    }

    @Override
    public String generateToken(String username) {
        UserDetails userDetails = loadUserDetailsByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllUsersWithRoles();
    }

    // Helper method to create UserDetails from User entity
    private UserDetails loadUserDetailsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String roleName = role.name();
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
