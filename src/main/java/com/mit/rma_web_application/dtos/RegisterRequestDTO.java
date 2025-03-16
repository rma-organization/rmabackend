package com.mit.rma_web_application.dtos;


import com.mit.rma_web_application.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    private String username;
    private String email;
    private String password;
    private Set<Role> roles; // Ensure this matches the expected roles
}
