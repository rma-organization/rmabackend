package com.mit.rma_web_application.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_role")
    private String receiverRole;

    private String message;

    private String type;

    @Column(name = "is_read")
    private boolean read;

    private LocalDateTime timestamp;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "status")
    private String status;

    public Notification() {
        this.read = false;
        this.timestamp = LocalDateTime.now();
    }
}
