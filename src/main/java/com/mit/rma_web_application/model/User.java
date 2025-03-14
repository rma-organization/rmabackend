package com.mit.rma_web_application.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING; // Default to PENDING

    private LocalDateTime registeredAt = LocalDateTime.now();
    private LocalDateTime approvedAt;
    private LocalDateTime deletedAt;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User(String username, String email, String password, Set<Role> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.approvalStatus = ApprovalStatus.PENDING; // Default approval status
        this.registeredAt = LocalDateTime.now();
    }

    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;

        if (approvalStatus == ApprovalStatus.APPROVED) {
            this.approvedAt = LocalDateTime.now();
        } else if (approvalStatus == ApprovalStatus.REJECTED) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
