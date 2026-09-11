package com.david.monitoring.metrics;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private MetricService metricService;

    private ServiceEntity createService(Long id) throws Exception {
        ServiceEntity service = new ServiceEntity(1L, "API", "https://example.com");
        Field idField = ServiceEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(service, id);
        return service;
    }

    @Test
    void saveMetricReturnsSavedMetric() throws Exception {
        ServiceEntity service = createService(1L);

        Metric saved = new Metric(service, 150L, 200, 1.0);
        when(metricRepository.save(any(Metric.class))).thenReturn(saved);

        Metric result = metricService.saveMetric(service, 150L, 200, 1.0);

        assertEquals(150L, result.getLatencyMs());
        assertEquals(200, result.getStatusCode());
        assertEquals(1.0, result.getAvailability());
        verify(metricRepository).save(any(Metric.class));
    }

    @Test
    void getMetricsForService() throws Exception {
        ServiceEntity service = createService(1L);

        Metric m1 = new Metric(service, 100L, 200, 1.0);
        when(metricRepository.findByServiceOrderByCreatedAtDesc(service)).thenReturn(List.of(m1));

        List<Metric> result = metricService.getMetricsForService(service);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getLatencyMs());
    }

    @Test
    void getMetricsLastMinutes() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findRecentMetrics(eq(service), any())).thenReturn(List.of());

        List<Metric> result = metricService.getMetricsLastMinutes(service, 60);

        assertNotNull(result);
        verify(metricRepository).findRecentMetrics(eq(service), any());
    }

    @Test
    void getAverageLatencyLastHour() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findAverageLatency(eq(service), any())).thenReturn(125.5);

        Double result = metricService.getAverageLatencyLastHour(service);

        assertEquals(125.5, result);
    }
}
