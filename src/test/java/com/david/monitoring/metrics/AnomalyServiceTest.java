package com.david.monitoring.metrics;

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
class AnomalyServiceTest {

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private AnomalyService anomalyService;

    private ServiceEntity createService(Long id) throws Exception {
        ServiceEntity service = new ServiceEntity(1L, "API", "https://example.com");
        Field idField = ServiceEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(service, id);
        return service;
    }

    @Test
    void detectsAnomalyWhenRecentHigh() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findLatenciesSince(eq(1L), any()))
                .thenReturn(List.of(900L, 950L, 1000L))
                .thenReturn(List.of(100L, 120L, 110L, 130L));

        assertTrue(anomalyService.isAnomalous(service));
    }

    @Test
    void noAnomalyWhenLatencyNormal() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findLatenciesSince(eq(1L), any()))
                .thenReturn(List.of(150L, 160L, 170L))
                .thenReturn(List.of(100L, 120L, 110L, 130L));

        assertFalse(anomalyService.isAnomalous(service));
    }

    @Test
    void noAnomalyWhenRecentEmpty() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findLatenciesSince(eq(1L), any()))
                .thenReturn(List.of())
                .thenReturn(List.of(100L, 120L));

        assertFalse(anomalyService.isAnomalous(service));
    }

    @Test
    void noAnomalyWhenHistoricalEmpty() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findLatenciesSince(eq(1L), any()))
                .thenReturn(List.of(900L, 950L))
                .thenReturn(List.of());

        assertFalse(anomalyService.isAnomalous(service));
    }

    @Test
    void noAnomalyWhenBothEmpty() throws Exception {
        ServiceEntity service = createService(1L);

        when(metricRepository.findLatenciesSince(eq(1L), any()))
                .thenReturn(List.of())
                .thenReturn(List.of());

        assertFalse(anomalyService.isAnomalous(service));
    }
}
