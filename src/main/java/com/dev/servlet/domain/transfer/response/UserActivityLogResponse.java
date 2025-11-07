package com.dev.servlet.domain.transfer.response;

import com.dev.servlet.domain.model.enums.ActivityStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


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
    private Date timestamp;
    private String userAgent;
}
