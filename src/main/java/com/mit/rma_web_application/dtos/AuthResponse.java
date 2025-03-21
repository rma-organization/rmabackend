package com.mit.rma_web_application.dtos;


import com.mit.rma_web_application.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;  // JWT token
    private Role role;   // Role of the authenticated user
}
