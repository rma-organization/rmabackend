

package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterRequestDTO registrationDto);
    boolean existsByUsername(String username);
    User findByUsername(String username);
    List<User> getPendingUsers();
    User approveUser(Long userId);
    String generateToken(String username);
    User save(User user);
    List<User> getAllUsers();
}
