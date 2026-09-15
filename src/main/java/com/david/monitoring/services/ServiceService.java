package com.david.monitoring.services;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import com.david.monitoring.metrics.MetricRepository;
import com.david.monitoring.services.dto.ServiceResponse;
import com.david.monitoring.services.dto.CreateServiceRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository repository;
    private final MetricRepository metricRepository;
    private final ServiceUrlValidator serviceUrlValidator;

    public ServiceService(ServiceRepository repository, MetricRepository metricRepository, ServiceUrlValidator serviceUrlValidator) {
        this.repository = repository;
        this.metricRepository = metricRepository;
        this.serviceUrlValidator = serviceUrlValidator;
    }

    @Transactional
    public ServiceResponse create(Long userId, CreateServiceRequest request) {
        serviceUrlValidator.validate(request.url());
        ServiceEntity entity = new ServiceEntity(userId, request.name(), request.url());
        return toResponse(repository.save(entity));
    }

    public List<ServiceResponse> list(Long userId) {
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public ServiceResponse get(Long userId, Long id) {
        return toResponse(findByIdOrForbidden(userId, id));
    }

    @Transactional
    public void delete(Long userId, Long id) {
        ServiceEntity entity = findByIdOrForbidden(userId, id);
        metricRepository.deleteByServiceId(entity.getId());
        repository.delete(entity);
    }

    @Transactional
    public ServiceResponse update(Long userId, Long id, CreateServiceRequest request) {
        serviceUrlValidator.validate(request.url());
        ServiceEntity entity = findByIdOrForbidden(userId, id);
        entity.setName(request.name());
        entity.setUrl(request.url());
        return toResponse(repository.save(entity));
    }

    public List<ServiceEntity> findAllServices() {
        return repository.findAll();
    }

    public ServiceEntity getEntityOrForbidden(Long userId, Long id) {
        return findByIdOrForbidden(userId, id);
    }

    private ServiceEntity findByIdOrForbidden(Long userId, Long id) {
        ServiceEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!entity.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return entity;
    }

    private ServiceResponse toResponse(ServiceEntity entity) {
        Metric latest = metricRepository.findTopByServiceOrderByCreatedAtDesc(entity).orElse(null);
        return new ServiceResponse(
                entity.getId(),
                entity.getName(),
                entity.getUrl(),
                entity.getCreatedAt(),
                latest != null ? latest.getLatencyMs() : null,
                latest != null ? latest.getStatusCode() : null,
                latest != null ? latest.getAvailability() : null
        );
    }
}
