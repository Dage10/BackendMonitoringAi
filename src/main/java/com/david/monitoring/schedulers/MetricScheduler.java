package com.david.monitoring.schedulers;

import com.david.monitoring.config.AuditLogger;
import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import com.david.monitoring.metrics.AnomalyService;
import com.david.monitoring.metrics.MetricCollectorService;
import com.david.monitoring.metrics.MetricsStreamService;
import com.david.monitoring.services.ServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class MetricScheduler {

    private static final Logger log = LoggerFactory.getLogger(MetricScheduler.class);

    private final ServiceService serviceService;
    private final MetricCollectorService metricCollectorService;
    private final MetricsStreamService metricsStreamService;
    private final AnomalyService anomalyService;
    private final AuditLogger auditLogger;
    private final ExecutorService executor;

    public MetricScheduler(ServiceService serviceService,
                           MetricCollectorService metricCollectorService,
                           MetricsStreamService metricsStreamService,
                           AnomalyService anomalyService,
                           AuditLogger auditLogger) {
        this.serviceService = serviceService;
        this.metricCollectorService = metricCollectorService;
        this.metricsStreamService = metricsStreamService;
        this.anomalyService = anomalyService;
        this.auditLogger = auditLogger;
        this.executor = Executors.newFixedThreadPool(4);
    }

    @Scheduled(fixedRate = 30_000)
    public void collectMetrics() {
        List<ServiceEntity> services = serviceService.findAllServices();

        for (ServiceEntity service : services) {
            executor.submit(() -> {
                try {
                    Metric metric = metricCollectorService.collect(service);
                    metricsStreamService.sendMetric(service.getUserId(), metric);

                    boolean anomaly = anomalyService.isAnomalous(service);
                    if (anomaly) {
                        metricsStreamService.sendAlert(service.getUserId(), service.getName());
                        auditLogger.anomalyDetected(service.getUserId(), service.getId(), "Latency spike on " + service.getName());
                    }
                } catch (Exception e) {
                    log.error("Failed to collect metrics for service: {}", service.getName(), e);
                }
            });
        }
    }
}