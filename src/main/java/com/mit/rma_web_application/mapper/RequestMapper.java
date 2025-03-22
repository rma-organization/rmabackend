package com.mit.rma_web_application.mapper;

import com.mit.rma_web_application.dto.RequestDTO;
import com.mit.rma_web_application.dto.VendorDTO;
import com.mit.rma_web_application.dto.CustomerDTO;
import com.mit.rma_web_application.model.Request;
import com.mit.rma_web_application.model.Vendor;
import com.mit.rma_web_application.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    private static final Logger logger = LoggerFactory.getLogger(RequestMapper.class);

    public RequestDTO toDTO(Request request) {
        if (request == null) {
            return null;
        }

        logger.debug("Mapping Request entity to RequestDTO for ID: {}", request.getId());
        RequestDTO dto = new RequestDTO();

        dto.setId(request.getId());
        dto.setName(request.getName());
        dto.setStatus(request.getStatus());
        dto.setPartId(request.getPartId());
        dto.setSrNumber(request.getSrNumber());
        dto.setFieldServiceTaskNumber(request.getFieldServiceTaskNumber());
        dto.setFaultPartNumber(request.getFaultPartNumber());
        dto.setMailIds(request.getMailIds());
        dto.setRequestedUserId(request.getRequestedUserId());
        dto.setDescription(request.getDescription());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setDeletedAt(request.getDeletedAt());

        if (request.getVendor() != null) {
            dto.setVendor(toVendorDTO(request.getVendor()));
        }

        if (request.getCustomer() != null) {
            dto.setCustomer(toCustomerDTO(request.getCustomer()));
        }

        return dto;
    }

    public Request toEntity(RequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        logger.debug("Mapping RequestDTO to Request entity");
        Request request = new Request();

        request.setId(requestDTO.getId());
        request.setName(requestDTO.getName());
        request.setStatus(requestDTO.getStatus());
        request.setPartId(requestDTO.getPartId());
        request.setSrNumber(requestDTO.getSrNumber());
        request.setFieldServiceTaskNumber(requestDTO.getFieldServiceTaskNumber());
        request.setFaultPartNumber(requestDTO.getFaultPartNumber());
        request.setMailIds(requestDTO.getMailIds());
        request.setRequestedUserId(requestDTO.getRequestedUserId());
        request.setDescription(requestDTO.getDescription());
        request.setCreatedAt(requestDTO.getCreatedAt());
        request.setUpdatedAt(requestDTO.getUpdatedAt());
        request.setDeletedAt(requestDTO.getDeletedAt());

        if (requestDTO.getVendor() != null) {
            request.setVendor(toVendorEntity(requestDTO.getVendor()));
        }

        if (requestDTO.getCustomer() != null) {
            request.setCustomer(toCustomerEntity(requestDTO.getCustomer()));
        }

        return request;
    }

    public void updateEntityFromDTO(RequestDTO requestDTO, Request request) {
        if (requestDTO == null || request == null) {
            logger.warn("Cannot update Request entity - RequestDTO or Request entity is null");
            return;
        }

        logger.debug("Updating Request entity with ID: {}", request.getId());

        if (requestDTO.getName() != null) {
            request.setName(requestDTO.getName());
        }
        if (requestDTO.getStatus() != null) {
            request.setStatus(requestDTO.getStatus());
        }
        if (requestDTO.getPartId() != null) {
            request.setPartId(requestDTO.getPartId());
        }
        if (requestDTO.getSrNumber() != null) {
            request.setSrNumber(requestDTO.getSrNumber());
        }
        if (requestDTO.getFieldServiceTaskNumber() != null) {
            request.setFieldServiceTaskNumber(requestDTO.getFieldServiceTaskNumber());
        }
        if (requestDTO.getFaultPartNumber() != null) {
            request.setFaultPartNumber(requestDTO.getFaultPartNumber());
        }
        if (requestDTO.getMailIds() != null) {
            request.setMailIds(requestDTO.getMailIds());
        }
        if (requestDTO.getRequestedUserId() != null) {
            request.setRequestedUserId(requestDTO.getRequestedUserId());
        }
        if (requestDTO.getDescription() != null) {
            request.setDescription(requestDTO.getDescription());
        }

        if (requestDTO.getVendor() != null) {
            if (request.getVendor() == null) {
                request.setVendor(new Vendor());
            }
            request.getVendor().setId(requestDTO.getVendor().getId());
            request.getVendor().setName(requestDTO.getVendor().getName());
        }

        if (requestDTO.getCustomer() != null) {
            if (request.getCustomer() == null) {
                request.setCustomer(new Customer());
            }
            request.getCustomer().setId(requestDTO.getCustomer().getId());
            request.getCustomer().setName(requestDTO.getCustomer().getName());
        }

        request.setUpdatedAt(requestDTO.getUpdatedAt());
    }

    private VendorDTO toVendorDTO(Vendor vendor) {
        if (vendor == null) {
            return null;
        }

        logger.debug("Mapping Vendor entity to VendorDTO for ID: {}", vendor.getId());
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setId(vendor.getId());
        vendorDTO.setName(vendor.getName());
        return vendorDTO;
    }

    private Vendor toVendorEntity(VendorDTO vendorDTO) {
        if (vendorDTO == null) {
            return null;
        }

        logger.debug("Mapping VendorDTO to Vendor entity for ID: {}", vendorDTO.getId());
        Vendor vendor = new Vendor();
        vendor.setId(vendorDTO.getId());
        vendor.setName(vendorDTO.getName());
        return vendor;
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        logger.debug("Mapping Customer entity to CustomerDTO for ID: {}", customer.getId());
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        return customerDTO;
    }

    private Customer toCustomerEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        logger.debug("Mapping CustomerDTO to Customer entity for ID: {}", customerDTO.getId());
        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setName(customerDTO.getName());
        return customer;
    }
}