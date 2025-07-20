package com.mit.rma_web_application.dtos;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    // getters and setters
}