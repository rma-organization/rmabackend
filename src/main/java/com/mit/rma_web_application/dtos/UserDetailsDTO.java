
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

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
}
