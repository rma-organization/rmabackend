package com.mit.rma_web_application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class CustomerDTO {
    private Long id;
    private String name;
    public CustomerDTO() {}


}