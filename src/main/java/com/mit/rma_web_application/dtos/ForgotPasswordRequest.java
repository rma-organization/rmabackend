package com.mit.rma_web_application.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    private String username;
    private String email;

    // getters and setters
}