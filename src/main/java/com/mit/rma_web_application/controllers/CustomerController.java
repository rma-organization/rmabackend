package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.CustomerDTO;
import com.mit.rma_web_application.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    // Constructor Injection
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Get all customers
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    // Get a customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long customerId) {
        CustomerDTO customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    // Create a new customer
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO createdCustomer = customerService.addCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    // Update a customer
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(customerId, customerDTO);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Delete a customer
    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return ResponseEntity.ok("Customer deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Restore a deleted customer
    @PatchMapping("/{customerId}/restore")
    public ResponseEntity<String> restoreCustomer(@PathVariable Long customerId) {
        try {
            customerService.restoreCustomer(customerId);
            return ResponseEntity.ok("Customer restored successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
