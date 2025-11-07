package com.dev.servlet.domain.model;

import com.dev.servlet.domain.model.enums.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

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
    @GeneratedValue(generator = "short-uuid")
    @GenericGenerator(name = "short-uuid", strategy = "com.dev.servlet.core.util.ShortUuidGenerator")
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

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

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "correlation_id", length = 50)
    private String correlationId;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;
}

