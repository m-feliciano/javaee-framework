package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.domain.entity.enums.ActivityStatus;
import com.dev.servlet.shared.vo.AuditPayload;

import java.util.HashMap;

public interface LogUserActivityPort {

    void logActivity(String userId, ActivityStatus outcome, AuditPayload<?, ?> payload, HashMap<String, Object> metadata);
}

