package com.servletstack.application.transfer.response;

import com.servletstack.domain.entity.enums.ActivityStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
public class UserActivityLogResponse {
    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private String entityId;
    private ActivityStatus status;
    private String requestPayload;
    private String responsePayload;
    private String errorMessage;
    private Integer httpStatusCode;
    private String httpMethod;
    private String endpoint;
    private String ipAddress;
    private String correlationId;
    private Long executionTimeMs;
    private LocalDate timestamp;
    private String userAgent;

    @JsonIgnore
    public String getTimestampFormatted() {
        return timestamp != null ? timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
}
