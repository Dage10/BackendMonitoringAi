package com.david.monitoring.metrics.dto;

import java.time.Instant;

public record MetricResponse(
        long latencyMs,
        int statusCode,
        double availability,
        Instant createdAt
) {}
