package com.david.monitoring.services.dto;

import java.time.Instant;

public record ServiceResponse(
        Long id,
        String name,
        String url,
        Instant createdAt,
        Long lastLatencyMs,
        Integer lastStatusCode,
        Double lastAvailability
) {}
