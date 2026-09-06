package com.david.monitoring.metrics;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import com.david.monitoring.metrics.dto.MetricResponse;
import com.david.monitoring.services.ServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/metrics")
@Validated
public class MetricController {

    private final MetricRepository metricRepository;
    private final ServiceRepository serviceRepository;

    public MetricController(MetricRepository metricRepository, ServiceRepository serviceRepository) {
        this.metricRepository = metricRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/service/{serviceId}")
    public List<MetricResponse> getMetrics(Authentication auth,
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "60") @Min(1) @Max(10080) int minutes) {
        ServiceEntity service = findServiceOrForbidden((Long) auth.getPrincipal(), serviceId);
        Instant from = Instant.now().minus(Duration.ofMinutes(minutes));
        return metricRepository.findRecentMetrics(service, from).stream()
                .map(this::toResponse)
                .toList();
    }

    private ServiceEntity findServiceOrForbidden(Long userId, Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!service.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return service;
    }

    private MetricResponse toResponse(Metric metric) {
        return new MetricResponse(
                metric.getLatencyMs(),
                metric.getStatusCode(),
                metric.getAvailability(),
                metric.getCreatedAt()
        );
    }
}
