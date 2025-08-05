package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.CustomerDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();
    CustomerDTO addCustomer(CustomerDTO customerDTO);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    void deleteCustomer(Long id);
    void restoreCustomer(Long id);
}
