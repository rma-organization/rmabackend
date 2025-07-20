package com.mit.rma_web_application.services.interfaces;

import com.mit.rma_web_application.dtos.DashboardResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;

import java.util.List;
import java.util.Map;

public interface IUserService {
    User registerUser(RegisterRequestDTO user);

    boolean existsByUsername(String username);

    User findByUsername(String username);

    List<User> getPendingUsers();

    User approveUser(Long userId);

    String generateToken(String username);

    User save(User user);

    List<User> getAllUsers();

    Map<String, Object> getUserStatistics();

    DashboardResponse getDashboardData();
}
