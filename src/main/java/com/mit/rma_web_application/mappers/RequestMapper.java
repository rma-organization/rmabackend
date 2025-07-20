

package com.mit.rma_web_application.mappers;

import com.mit.rma_web_application.dtos.RequestDTO;
import com.mit.rma_web_application.dtos.VendorDTO;
import com.mit.rma_web_application.dtos.CustomerDTO;
import com.mit.rma_web_application.models.Request;
import com.mit.rma_web_application.models.Vendor;
import com.mit.rma_web_application.models.Customer;
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
        dto.setRequestedBy(request.getRequestedBy()); // ✅ added
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

    public Request toEntity(RequestDTO dto) {
        if (dto == null) {
            return null;
        }

        logger.debug("Mapping RequestDTO to Request entity");
        Request request = new Request();

        request.setId(dto.getId());
        request.setName(dto.getName());
        request.setStatus(dto.getStatus());
        request.setPartId(dto.getPartId());
        request.setSrNumber(dto.getSrNumber());
        request.setFieldServiceTaskNumber(dto.getFieldServiceTaskNumber());
        request.setFaultPartNumber(dto.getFaultPartNumber());
        request.setMailIds(dto.getMailIds());
        request.setRequestedUserId(dto.getRequestedUserId());
        request.setRequestedBy(dto.getRequestedBy()); // ✅ added
        request.setDescription(dto.getDescription());
        request.setCreatedAt(dto.getCreatedAt());
        request.setUpdatedAt(dto.getUpdatedAt());
        request.setDeletedAt(dto.getDeletedAt());

        if (dto.getVendor() != null) {
            request.setVendor(toVendorEntity(dto.getVendor()));
        }

        if (dto.getCustomer() != null) {
            request.setCustomer(toCustomerEntity(dto.getCustomer()));
        }

        return request;
    }

    public void updateEntityFromDTO(RequestDTO dto, Request request) {
        if (dto == null || request == null) {
            logger.warn("Cannot update Request entity - RequestDTO or Request entity is null");
            return;
        }

        logger.debug("Updating Request entity with ID: {}", request.getId());

        if (dto.getName() != null) request.setName(dto.getName());
        if (dto.getStatus() != null) request.setStatus(dto.getStatus());
        if (dto.getPartId() != null) request.setPartId(dto.getPartId());
        if (dto.getSrNumber() != null) request.setSrNumber(dto.getSrNumber());
        if (dto.getFieldServiceTaskNumber() != null) request.setFieldServiceTaskNumber(dto.getFieldServiceTaskNumber());
        if (dto.getFaultPartNumber() != null) request.setFaultPartNumber(dto.getFaultPartNumber());
        if (dto.getMailIds() != null) request.setMailIds(dto.getMailIds());
        if (dto.getRequestedUserId() != null) request.setRequestedUserId(dto.getRequestedUserId());
        if (dto.getRequestedBy() != null) request.setRequestedBy(dto.getRequestedBy()); // ✅ added
        if (dto.getDescription() != null) request.setDescription(dto.getDescription());

        if (dto.getVendor() != null) {
            if (request.getVendor() == null) request.setVendor(new Vendor());
            request.getVendor().setId(dto.getVendor().getId());
            request.getVendor().setName(dto.getVendor().getName());
        }

        if (dto.getCustomer() != null) {
            if (request.getCustomer() == null) request.setCustomer(new Customer());
            request.getCustomer().setId(dto.getCustomer().getId());
            request.getCustomer().setName(dto.getCustomer().getName());
        }

        request.setUpdatedAt(dto.getUpdatedAt());
    }

    private VendorDTO toVendorDTO(Vendor vendor) {
        if (vendor == null) return null;
        logger.debug("Mapping Vendor entity to VendorDTO for ID: {}", vendor.getId());
        VendorDTO dto = new VendorDTO();
        dto.setId(vendor.getId());
        dto.setName(vendor.getName());
        return dto;
    }

    private Vendor toVendorEntity(VendorDTO dto) {
        if (dto == null) return null;
        logger.debug("Mapping VendorDTO to Vendor entity for ID: {}", dto.getId());
        Vendor vendor = new Vendor();
        vendor.setId(dto.getId());
        vendor.setName(dto.getName());
        return vendor;
    }

    private CustomerDTO toCustomerDTO(Customer customer) {
        if (customer == null) return null;
        logger.debug("Mapping Customer entity to CustomerDTO for ID: {}", customer.getId());
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        return dto;
    }

    private Customer toCustomerEntity(CustomerDTO dto) {
        if (dto == null) return null;
        logger.debug("Mapping CustomerDTO to Customer entity for ID: {}", dto.getId());
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        return customer;
    }
}
