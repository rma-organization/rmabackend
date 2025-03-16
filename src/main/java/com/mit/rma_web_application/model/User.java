//package com.mit.rma_web_application.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//
//@Entity
//@NoArgsConstructor
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String username;  // Explicitly declare without final for setters
//    private String email;
//    private String password;
//
//    @Enumerated(EnumType.STRING)
//    private ApprovalStatus approvalStatus;
//
//    private LocalDateTime registeredAt;
//    private LocalDateTime approvedAt;
//    private LocalDateTime deletedAt;
//
//    @ManyToMany
//    @JoinTable(
//            name = "user_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id"))
//    private Set<Role> roles;
//
//    // Constructor with fields
//    public User(String username, String email, String password, Set<Role> roles) {
//        this.username = username;
//        this.email = email;
//        this.password = password;
//        this.roles = roles;
//        this.approvalStatus = ApprovalStatus.PENDING;
//        this.registeredAt = LocalDateTime.now();
//    }
//
//    // Getter for approvalStatus
//    public ApprovalStatus getApprovalStatus() {
//        return approvalStatus;
//    }
//
//    // Setter for approvalStatus
//    public void setApprovalStatus(ApprovalStatus approvalStatus) {
//        this.approvalStatus = approvalStatus;
//        if (approvalStatus == ApprovalStatus.APPROVED) {
//            this.approvedAt = LocalDateTime.now();
//        } else if (approvalStatus == ApprovalStatus.REJECTED) {
//            this.deletedAt = LocalDateTime.now();
//        }
//    }
//
//    // Getter for registeredAt
//    public LocalDateTime getRegisteredAt() {
//        return registeredAt;
//    }
//
//    // Getter for approvedAt
//    public LocalDateTime getApprovedAt() {
//        return approvedAt;
//    }
//
//    // Getter for deletedAt
//    public LocalDateTime getDeletedAt() {
//        return deletedAt;
//    }
//
//    // Getter for username
//    public String getUsername() {
//        return username;
//    }
//
//    // Setter for username
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    // Getter for email
//    public String getEmail() {
//        return email;
//    }
//
//    // Setter for email
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    // Getter for password
//    public String getPassword() {
//        return password;
//    }
//
//    // Setter for password
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    // Getter for roles
//    public Set<Role> getRoles() {
//        return roles;
//    }
//
//    // Setter for roles
//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }
//
//    // Method to check if the user is approved
//    public boolean isApproved() {
//        return approvalStatus == ApprovalStatus.APPROVED;
//    }
//}
package com.mit.rma_web_application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private LocalDateTime registeredAt;
    private LocalDateTime approvedAt;
    private LocalDateTime deletedAt;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // Constructor with fields
    public User(String username, String email, String password, Set<Role> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.registeredAt = LocalDateTime.now();
    }

    // Getter for approvalStatus
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    // Setter for approvalStatus
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
        if (approvalStatus == ApprovalStatus.APPROVED) {
            this.approvedAt = LocalDateTime.now();
        } else if (approvalStatus == ApprovalStatus.REJECTED) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    // Getter for registeredAt
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    // Getter for approvedAt
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    // Getter for deletedAt
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for roles
    public Set<Role> getRoles() {
        return roles;
    }

    // Setter for roles
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Method to check if the user is approved
    public boolean isApproved() {
        return approvalStatus == ApprovalStatus.APPROVED;
    }
}
