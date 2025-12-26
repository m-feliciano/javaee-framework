package com.dev.servlet.domain.entity;

import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user_activity_log")
public class UserActivityLog {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;
    @Column(name = "user_id", updatable = false)
    private UUID userId;
    @Column(name = "action", nullable = false, length = 100)
    private String action;
    @Column(name = "entity_type", length = 50)
    private String entityType;
    @Column(name = "entity_id")
    private String entityId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ActivityStatus status;
    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;
    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    @Column(name = "http_status_code")
    private Integer httpStatusCode;
    @Column(name = "http_method", length = 10)
    private String httpMethod;
    @Column(name = "endpoint")
    private String endpoint;
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @Column(name = "correlation_id", length = 50)
    private String correlationId;
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}
