package com.mit.rma_web_application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class CustomerDTO {
    private Long id;
    private String name;
    public CustomerDTO() {}


}