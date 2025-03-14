package com.mit.rma_web_application.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(RoleName name) {
        this.name = name;
    }

    public enum RoleName {
        ADMIN,
        ENGINEER,
        RMA,
        SUPPLYCHAIN
    }
}
