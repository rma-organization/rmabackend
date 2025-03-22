package com.mit.rma_web_application.mapper;

import com.mit.rma_web_application.dto.CustomerDTO;
import com.mit.rma_web_application.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    // Converts Customer entity to CustomerDTO
    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        return customerDTO;
    }

    // Converts CustomerDTO to Customer entity
    public Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setName(customerDTO.getName());
        return customer;
    }
}
