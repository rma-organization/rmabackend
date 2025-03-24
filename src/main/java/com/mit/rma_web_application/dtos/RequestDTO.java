package com.mit.rma_web_application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestDTO {

    private Long id;
    private String name;
    private String status;
    private Long partId;
    private VendorDTO vendor;
    private String srNumber;
    private String fieldServiceTaskNumber;
    private String faultPartNumber;
    private String mailIds;
    private CustomerDTO customer;
    private Integer requestedUserId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}