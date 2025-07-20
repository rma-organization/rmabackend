package com.mit.rma_web_application.services.interfaces;

import com.mit.rma_web_application.dtos.DashboardResponse;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;

import java.util.Map;

public interface IUserService {
    User registerUser(RegisterRequestDTO user);

    Map<String, Object> getUserStatistics();

    DashboardResponse getDashboardData();
}
