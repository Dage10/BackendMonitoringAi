package com.david.monitoring.services;

import com.david.monitoring.config.AuditLogger;
import com.david.monitoring.services.dto.CreateServiceRequest;
import com.david.monitoring.services.dto.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    private ServiceController controller;

    @Mock
    private ServiceService serviceService;

    @Mock
    private AuditLogger auditLogger;

    @BeforeEach
    void setUp() {
        controller = new ServiceController(serviceService, auditLogger);
    }

    private UsernamePasswordAuthenticationToken auth(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, List.of());
    }

    private CreateServiceRequest createRequest() {
        return new CreateServiceRequest("My API", "https://example.com");
    }

    private ServiceResponse serviceResponse() {
        return new ServiceResponse(1L, "My API", "https://example.com",
                Instant.now(), 150L, 200, 1.0);
    }

    @Test
    void createServiceReturnsOk() {
        when(serviceService.create(eq(1L), any(CreateServiceRequest.class)))
                .thenReturn(serviceResponse());

        ResponseEntity<?> response = controller.create(auth(1L), createRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditLogger).serviceCreated(eq(1L), any(), any());
    }

    @Test
    void listServicesReturnsOk() {
        when(serviceService.list(1L)).thenReturn(List.of(serviceResponse()));

        ResponseEntity<?> response = controller.list(auth(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getServiceReturnsOk() {
        when(serviceService.get(1L, 1L)).thenReturn(serviceResponse());

        ResponseEntity<?> response = controller.get(auth(1L), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getServiceNotFound() {
        when(serviceService.get(1L, 99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class,
                () -> controller.get(auth(1L), 99L));
    }

    @Test
    void deleteServiceReturnsNoContent() {
        doNothing().when(serviceService).delete(1L, 1L);

        ResponseEntity<?> response = controller.delete(auth(1L), 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(auditLogger).serviceDeleted(1L, 1L);
    }

    @Test
    void updateServiceReturnsOk() {
        ServiceResponse updated = new ServiceResponse(1L, "Updated API", "https://updated.com",
                Instant.now(), 200L, 200, 1.0);

        when(serviceService.update(eq(1L), eq(1L), any(CreateServiceRequest.class)))
                .thenReturn(updated);

        ResponseEntity<?> response = controller.update(auth(1L), 1L,
                new CreateServiceRequest("Updated API", "https://updated.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditLogger).serviceUpdated(1L, 1L);
    }
}
