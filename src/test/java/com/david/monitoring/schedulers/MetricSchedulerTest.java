package com.david.monitoring.schedulers;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import com.david.monitoring.metrics.AnomalyService;
import com.david.monitoring.metrics.MetricCollectorService;
import com.david.monitoring.metrics.MetricsStreamService;
import com.david.monitoring.services.ServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricSchedulerTest {

    @Mock
    private ServiceService serviceService;

    @Mock
    private MetricCollectorService metricCollectorService;

    @Mock
    private MetricsStreamService metricsStreamService;

    @Mock
    private AnomalyService anomalyService;

    @InjectMocks
    private MetricScheduler metricScheduler;

    private ServiceEntity createService(Long id, Long userId, String name) throws Exception {
        ServiceEntity service = new ServiceEntity(userId, name, "https://" + name.toLowerCase() + ".com");
        Field idField = ServiceEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(service, id);
        return service;
    }

    @Test
    void collectMetricsFromAllServices() throws Exception {
        ServiceEntity service1 = createService(1L, 1L, "API 1");
        ServiceEntity service2 = createService(2L, 2L, "API 2");

        when(serviceService.findAllServices()).thenReturn(List.of(service1, service2));

        Metric metric1 = new Metric(service1, 100L, 200, 1.0);
        Metric metric2 = new Metric(service2, 150L, 200, 1.0);
        when(metricCollectorService.collect(service1)).thenReturn(metric1);
        when(metricCollectorService.collect(service2)).thenReturn(metric2);
        when(anomalyService.isAnomalous(any())).thenReturn(false);

        metricScheduler.collectMetrics();

        verify(metricCollectorService).collect(service1);
        verify(metricCollectorService).collect(service2);
        verify(metricsStreamService).sendMetric(1L, metric1);
        verify(metricsStreamService).sendMetric(2L, metric2);
    }

    @Test
    void sendsAlertOnAnomaly() throws Exception {
        ServiceEntity service = createService(1L, 1L, "API");

        when(serviceService.findAllServices()).thenReturn(List.of(service));

        Metric metric = new Metric(service, 900L, 200, 1.0);
        when(metricCollectorService.collect(service)).thenReturn(metric);
        when(anomalyService.isAnomalous(service)).thenReturn(true);

        metricScheduler.collectMetrics();

        verify(metricsStreamService).sendAlert(1L, "API");
    }

    @Test
    void handlesExceptionDuringCollection() throws Exception {
        ServiceEntity service = createService(1L, 1L, "API");

        when(serviceService.findAllServices()).thenReturn(List.of(service));
        when(metricCollectorService.collect(service)).thenThrow(new RuntimeException("Connection failed"));

        metricScheduler.collectMetrics();

        verify(metricsStreamService, never()).sendMetric(anyLong(), any());
    }
}
