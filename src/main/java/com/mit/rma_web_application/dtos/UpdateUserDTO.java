package com.mit.rma_web_application.dtos;

import com.mit.rma_web_application.models.ApprovalStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-arg constructor
@AllArgsConstructor // Generates an all-arg constructor
public class UpdateUserDTO {
    private Long id;
    private List<String> roles;
    private ApprovalStatus approvalStatus;
}
