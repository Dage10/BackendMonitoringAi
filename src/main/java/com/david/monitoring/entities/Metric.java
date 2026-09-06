package com.david.monitoring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "metrics",
        indexes = {
                @Index(name = "idx_metrics_service_created_at", columnList = "service_id, created_at")
        }
)
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(name = "availability", nullable = false)
    private double availability;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Metric() {
    }

    public Metric(ServiceEntity service, long latencyMs, int statusCode, double availability) {
        this.service = service;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
        this.availability = availability;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public ServiceEntity getService() {
        return service;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public double getAvailability() {
        return availability;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
