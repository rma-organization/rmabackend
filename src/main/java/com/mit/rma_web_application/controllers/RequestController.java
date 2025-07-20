

package com.mit.rma_web_application.controllers;

import com.mit.rma_web_application.dtos.RequestDTO;
import com.mit.rma_web_application.dtos.StatusUpdateDTO;
import com.mit.rma_web_application.services.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<RequestDTO>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RequestDTO> createRequest(@Valid @RequestBody RequestDTO dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.createRequest(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestDTO> updateRequest(@PathVariable Long id, @Valid @RequestBody RequestDTO dto) {
        return requestService.updateRequest(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        if (requestService.getRequestById(id).isEmpty()) return ResponseEntity.notFound().build();
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO dto,
            Principal principal,
            @RequestHeader("Role") String role
    ) {
        try {
            requestService.updateStatus(id, dto.getStatus(), principal.getName(), role);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error updating request status", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update status.");
        }
    }
}
