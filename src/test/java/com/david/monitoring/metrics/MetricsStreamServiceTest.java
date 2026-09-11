package com.david.monitoring.metrics;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetricsStreamServiceTest {

    private final MetricsStreamService streamService = new MetricsStreamService();

    private ServiceEntity createService(Long id) throws Exception {
        ServiceEntity service = new ServiceEntity(1L, "API", "https://example.com");
        Field idField = ServiceEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(service, id);
        return service;
    }

    @Test
    void addEmitterAndSendMetric() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        streamService.addEmitter(1L, emitter);

        ServiceEntity service = createService(1L);
        Metric metric = new Metric(service, 100L, 200, 1.0);

        streamService.sendMetric(1L, metric);

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void sendAlertToEmitter() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        streamService.addEmitter(1L, emitter);

        streamService.sendAlert(1L, "MyService");

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void sendToNoEmittersDoesNotThrow() throws Exception {
        ServiceEntity service = createService(1L);
        Metric metric = new Metric(service, 100L, 200, 1.0);

        assertDoesNotThrow(() -> streamService.sendMetric(99L, metric));
    }

    @Test
    void removeEmitterStopsSending() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        streamService.addEmitter(1L, emitter);
        streamService.removeEmitter(1L, emitter);

        ServiceEntity service = createService(1L);
        Metric metric = new Metric(service, 100L, 200, 1.0);

        streamService.sendMetric(1L, metric);

        verify(emitter, never()).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void failedSendRemovesEmitter() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        SseEmitter failingEmitter = mock(SseEmitter.class);
        doThrow(new IOException("Connection closed"))
                .when(failingEmitter).send(any(SseEmitter.SseEventBuilder.class));

        streamService.addEmitter(1L, emitter);
        streamService.addEmitter(1L, failingEmitter);

        ServiceEntity service = createService(1L);
        Metric metric = new Metric(service, 100L, 200, 1.0);

        streamService.sendMetric(1L, metric);

        verify(failingEmitter).complete();
        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }
}
