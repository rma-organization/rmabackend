package com.mit.rma_web_application.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorDTO {
    private Long id;
    @NotNull(message = "Vendor name is required")
    private String name;
}