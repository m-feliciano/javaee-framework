package com.servletstack.application.port.in.activity;

import com.servletstack.domain.entity.enums.ActivityStatus;
import com.servletstack.shared.vo.AuditPayload;

import java.util.HashMap;
import java.util.UUID;

public interface LogUserActivityUseCase {

    void logActivity(UUID userId, ActivityStatus outcome, AuditPayload<?, ?> payload, HashMap<String, Object> metadata);
}

