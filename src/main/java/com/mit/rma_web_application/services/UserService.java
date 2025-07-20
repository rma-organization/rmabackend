package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.DashboardResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService implements IUserService {

    private final IUserService userService;

    @Autowired
    public UserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        return userService.registerUser(registerRequestDTO);
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        return userService.getUserStatistics();
    }

    @Override
    public DashboardResponse getDashboardData() {
        return userService.getDashboardData();
    }
}
