package com.mit.rma_web_application.services.impl;

import com.mit.rma_web_application.dtos.CustomerDTO;
import com.mit.rma_web_application.exceptions.ResourceNotFoundException;
import com.mit.rma_web_application.models.Customer;
import com.mit.rma_web_application.repositories.CustomerRepository;
import com.mit.rma_web_application.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findByDeletedAtIsNull().stream()
                .map(customer -> new CustomerDTO(customer.getId(), customer.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());

        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerDTO(savedCustomer.getId(), savedCustomer.getName());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        return new CustomerDTO(customer.getId(), customer.getName());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        logger.info("Before Update: Customer ID = {}, Customer Name = {}", id, customer.getName());

        customer.setName(customerDTO.getName());

        Customer updatedCustomer = customerRepository.save(customer);

        logger.info("After Update: Customer ID = {}, Customer Name = {}", updatedCustomer.getId(), updatedCustomer.getName());

        return new CustomerDTO(updatedCustomer.getId(), updatedCustomer.getName());
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void restoreCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        customer.setDeletedAt(null);
        customerRepository.save(customer);
    }
}
