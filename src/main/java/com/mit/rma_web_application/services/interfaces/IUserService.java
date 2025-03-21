package com.mit.rma_web_application.services.interfaces;

import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;

public interface IUserService {
    User registerUser(RegisterRequestDTO user);
}
