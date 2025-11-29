package com.dev.servlet.application.transfer.response;

import com.dev.servlet.domain.entity.enums.ActivityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class UserActivityLogResponse {
    private String id;
    private String userId;
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

    public String getTimestampFormatted() {
        return timestamp != null ? timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
}
