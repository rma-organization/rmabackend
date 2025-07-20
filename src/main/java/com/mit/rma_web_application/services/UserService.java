package com.mit.rma_web_application.services;
import com.mit.rma_web_application.repositories.UserRepository;
import com.mit.rma_web_application.dtos.RegisterRequestDTO;
import com.mit.rma_web_application.models.User;
import com.mit.rma_web_application.services.interfaces.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;
    private final IUserService userService;

    // The facade delegates to the actual implementation
    public UserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        return userService.registerUser(registerRequestDTO);
    }

}
