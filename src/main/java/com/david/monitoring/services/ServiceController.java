package com.david.monitoring.services;

import com.david.monitoring.services.dto.CreateServiceRequest;
import com.david.monitoring.services.dto.ServiceResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService){
        this.serviceService = serviceService;
    }

    private Long userId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(Authentication auth,
            @Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.ok(serviceService.create(userId(auth), request));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> list(Authentication auth) {
        return ResponseEntity.ok(serviceService.list(userId(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> get(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(serviceService.get(userId(auth), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        serviceService.delete(userId(auth), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> update(Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.ok(serviceService.update(userId(auth), id, request));
    }
}
