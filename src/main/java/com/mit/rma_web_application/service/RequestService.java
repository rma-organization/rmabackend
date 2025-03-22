package com.mit.rma_web_application.service;

import com.mit.rma_web_application.dto.RequestDTO;
import com.mit.rma_web_application.mapper.RequestMapper;
import com.mit.rma_web_application.model.Request;
import com.mit.rma_web_application.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    public List<RequestDTO> getAllRequests() {
        log.info("Fetching all active requests...");
        return requestRepository.findAllActiveRequests().stream()
                .map(requestMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RequestDTO> getRequestById(Long id) {
        log.info("Fetching request by ID: {}", id);
        return requestRepository.findById(id)
                .filter(request -> request.getDeletedAt() == null)
                .map(requestMapper::toDTO);
    }

    public RequestDTO createRequest(RequestDTO requestDTO) {
        log.info("Creating new request: {}", requestDTO);
        Request request = requestMapper.toEntity(requestDTO);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toDTO(savedRequest);
    }

    @Transactional
    public void deleteRequest(Long id) {
        log.info("Soft deleting request with ID: {}", id);
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request with ID " + id + " not found"));
        request.setDeletedAt(LocalDateTime.now());
        requestRepository.save(request);
        log.info("Request soft deleted successfully at {}", request.getDeletedAt());
    }

    @Transactional
    public Optional<RequestDTO> updateRequest(Long id, RequestDTO requestDTO) {
        log.info("Updating request with ID: {}", id);
        return requestRepository.findById(id)
                .filter(request -> request.getDeletedAt() == null)
                .map(existingRequest -> { // Corrected variable name
                    requestMapper.updateEntityFromDTO(requestDTO, existingRequest); // Corrected variable name
                    existingRequest.setUpdatedAt(LocalDateTime.now()); // Corrected variable name
                    Request updatedRequest = requestRepository.save(existingRequest); // Corrected variable name
                    log.info("Request updated successfully: {}", updatedRequest);
                    return requestMapper.toDTO(updatedRequest);
                });
    }
}