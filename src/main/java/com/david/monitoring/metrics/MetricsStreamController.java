package com.david.monitoring.metrics;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class MetricsStreamController {

    private final MetricsStreamService metricsStreamService;

    public MetricsStreamController(MetricsStreamService metricsStreamService) {
        this.metricsStreamService = metricsStreamService;
    }

    private Long userId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping("/metrics/stream")
    public SseEmitter stream(Authentication auth) {
        Long userId = userId(auth);

        SseEmitter emitter = new SseEmitter(300_000L); // 5 minutes
        metricsStreamService.addEmitter(userId, emitter);

        emitter.onCompletion(() -> metricsStreamService.removeEmitter(userId, emitter));
        emitter.onTimeout(() -> metricsStreamService.removeEmitter(userId, emitter));

        return emitter;
    }
}