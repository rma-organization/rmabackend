package com.mit.rma_web_application.dtos;

import com.mit.rma_web_application.models.ApprovalStatus;
import java.util.List;

public class UserDetailsDTO {
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
    private ApprovalStatus approvalStatus;

    public UserDetailsDTO(Long id, String name, String email, List<String> roles, ApprovalStatus approvalStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters
}
