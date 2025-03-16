package com.mit.rma_web_application.dtos;

public class AuthResponse {
    private String token;  // JWT token
    private String role;   // Role of the authenticated user

    // Constructor
    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getter for token
    public String getToken() {
        return token;
    }

    // Setter for token
    public void setToken(String token) {
        this.token = token;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }
}
