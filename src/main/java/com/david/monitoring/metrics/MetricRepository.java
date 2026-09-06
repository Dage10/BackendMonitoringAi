package com.david.monitoring.metrics;

import com.david.monitoring.entities.Metric;
import com.david.monitoring.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {

    List<Metric> findByServiceOrderByCreatedAtDesc(ServiceEntity service);

    java.util.Optional<Metric> findTopByServiceOrderByCreatedAtDesc(ServiceEntity service);

    @Query("""
        SELECT m FROM Metric m
        WHERE m.service = :service
        AND m.createdAt >= :from
        ORDER BY m.createdAt DESC
    """)
    List<Metric> findRecentMetrics(ServiceEntity service, Instant from);

    @Query("""
        SELECT AVG(m.latencyMs) FROM Metric m
        WHERE m.service = :service
        AND m.createdAt >= :from
    """)
    Double findAverageLatency(ServiceEntity service, Instant from);

    @Query("""
        SELECT m.latencyMs
        FROM Metric m
        WHERE m.service.id = :serviceId
        AND m.createdAt >= :since
    """)
    List<Long> findLatenciesSince(Long serviceId, Instant since);

    @Modifying
    @Query("DELETE FROM Metric m WHERE m.service.id = :serviceId")
    void deleteByServiceId(Long serviceId);
}