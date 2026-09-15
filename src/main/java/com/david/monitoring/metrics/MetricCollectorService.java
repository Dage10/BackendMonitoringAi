package com.david.monitoring.metrics;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import com.david.monitoring.services.ServiceUrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MetricCollectorService {

    private static final Logger log = LoggerFactory.getLogger(MetricCollectorService.class);

    private final RestClient restClient;
    private final MetricService metricService;
    private final ServiceUrlValidator serviceUrlValidator;

    public MetricCollectorService(RestClient restClient, MetricService metricService, ServiceUrlValidator serviceUrlValidator) {
        this.restClient = restClient;
        this.metricService = metricService;
        this.serviceUrlValidator = serviceUrlValidator;
    }

    public Metric collect(ServiceEntity service) {
        long start = System.nanoTime();
        int statusCode;
        double availability;

        try {
            serviceUrlValidator.validate(service.getUrl());

            var response = restClient.get()
                    .uri(service.getUrl())
                    .retrieve()
                    .toEntity(String.class);

            statusCode = response.getStatusCode().value();
            availability = (statusCode >= 200 && statusCode < 300) ? 1.0 : 0.0;

        } catch (Exception e) {
            log.debug("Failed to collect metrics for service '{}': {}", service.getName(), e.getMessage());
            statusCode = 0;
            availability = 0.0;
        }

        long latencyMs = (System.nanoTime() - start) / 1_000_000;

        return metricService.saveMetric(service, latencyMs, statusCode, availability);
    }
}
