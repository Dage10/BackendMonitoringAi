package com.david.monitoring.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(
        name = "services",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_services_userid_name",
                        columnNames = {"user_id", "name"}
                ),
                @UniqueConstraint(
                        name = "uk_services_userid_url",
                        columnNames = {"user_id", "url"}
                )
        }
)
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Metric> metrics;

    protected ServiceEntity() {}

    public ServiceEntity(Long userId, String name, String url) {
        this.userId = userId;
        this.name = name;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
