package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.shared.vo.AuditPayload;

import java.util.HashMap;
import java.util.UUID;

public interface LogUserActivityUseCase {

    void logActivity(UUID userId, ActivityStatus outcome, AuditPayload<?, ?> payload, HashMap<String, Object> metadata);
}

