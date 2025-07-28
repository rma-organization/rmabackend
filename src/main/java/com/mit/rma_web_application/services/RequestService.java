//package com.mit.rma_web_application.services;
//
//import com.mit.rma_web_application.dtos.RequestDTO;
//import com.mit.rma_web_application.mappers.RequestMapper;
//import com.mit.rma_web_application.models.Request;
//import com.mit.rma_web_application.repositories.RequestRepository;
//import com.mit.rma_web_application.services.interfaces.NotificationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class RequestService {
//
//    private final RequestRepository requestRepository;
//    private final RequestMapper requestMapper;
//    private final NotificationService notificationService;
//
//    public List<RequestDTO> getAllRequests() {
//        return requestRepository.findAllActiveRequests().stream()
//                .map(requestMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public Optional<RequestDTO> getRequestById(Long id) {
//        return requestRepository.findById(id)
//                .filter(req -> req.getDeletedAt() == null)
//                .map(requestMapper::toDTO);
//    }
//
//    public RequestDTO createRequest(RequestDTO dto, String userName) {
//        dto.setRequestedBy(userName);
//        Request entity = requestMapper.toEntity(dto);
//        entity.setCreatedAt(LocalDateTime.now());
//        entity.setUpdatedAt(LocalDateTime.now());
//
//        Request saved = requestRepository.save(entity);
//
//        // Send notification to the supplychain role with 6 arguments
//        notificationService.sendNotification(
//                "supplychain",
//                "New part request by " + userName,
//                "REQUEST",
//                userName,
//                "Requested",
//                saved.getId()  // <-- Added missing 6th argument
//        );
//
//        return requestMapper.toDTO(saved);
//    }
//
//    @Transactional
//    public void deleteRequest(Long id) {
//        Request req = requestRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
//        req.setDeletedAt(LocalDateTime.now());
//        requestRepository.save(req);
//    }
//
//    @Transactional
//    public Optional<RequestDTO> updateRequest(Long id, RequestDTO dto) {
//        return requestRepository.findById(id)
//                .filter(r -> r.getDeletedAt() == null)
//                .map(r -> {
//                    requestMapper.updateEntityFromDTO(dto, r);
//                    r.setUpdatedAt(LocalDateTime.now());
//                    return requestMapper.toDTO(requestRepository.save(r));
//                });
//    }
//
//    @Transactional
//    public void updateStatus(Long id, String newStatus, String updatedBy, String role) {
//        Request req = requestRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
//        req.setStatus(newStatus);
//        req.setUpdatedAt(LocalDateTime.now());
//        requestRepository.save(req);
//
//        String message = "Request ID " + id + " status updated to '" + newStatus + "' by " + updatedBy;
//
//        // Send notification to the specified role with 6 arguments
//        notificationService.sendNotification(
//                role,
//                message,
//                "STATUS",
//                updatedBy,
//                newStatus,
//                id  // <-- Added missing 6th argument
//        );
//    }
//}
package com.mit.rma_web_application.services;

import com.mit.rma_web_application.dtos.RequestDTO;
import com.mit.rma_web_application.mappers.RequestMapper;
import com.mit.rma_web_application.models.Request;
import com.mit.rma_web_application.repositories.RequestRepository;
import com.mit.rma_web_application.services.interfaces.NotificationService;
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
    private final NotificationService notificationService;

    public List<RequestDTO> getAllRequests() {
        return requestRepository.findAllActiveRequests().stream()
                .map(requestMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RequestDTO> getRequestById(Long id) {
        return requestRepository.findById(id)
                .filter(req -> req.getDeletedAt() == null)
                .map(requestMapper::toDTO);
    }

    public RequestDTO createRequest(RequestDTO dto, String userName) {
        dto.setRequestedBy(userName);
        Request entity = requestMapper.toEntity(dto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        Request saved = requestRepository.save(entity);

        // Send notification to the supplychain role with 6 arguments
        notificationService.sendNotification(
                "supplychain",
                "New part request by " + userName,
                "REQUEST",
                userName,
                "Requested",
                saved.getId()
        );

        return requestMapper.toDTO(saved);
    }

    @Transactional
    public void deleteRequest(Long id) {
        Request req = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        req.setDeletedAt(LocalDateTime.now());
        requestRepository.save(req);
    }

    @Transactional
    public Optional<RequestDTO> updateRequest(Long id, RequestDTO dto) {
        return requestRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .map(r -> {
                    requestMapper.updateEntityFromDTO(dto, r);
                    r.setUpdatedAt(LocalDateTime.now());
                    return requestMapper.toDTO(requestRepository.save(r));
                });
    }

    @Transactional
    public void updateStatus(Long id, String newStatus, String updatedBy, String role) {
        Request req = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        req.setStatus(newStatus);
        req.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(req);

        String message = "Request ID " + id + " status updated to '" + newStatus + "' by " + updatedBy;

        // Send notification to the specified role with 6 arguments
        notificationService.sendNotification(
                role,
                message,
                "STATUS",
                updatedBy,
                newStatus,
                id
        );
    }
}
