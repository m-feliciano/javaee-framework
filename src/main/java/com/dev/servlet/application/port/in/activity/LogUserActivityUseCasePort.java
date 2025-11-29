package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.domain.entity.UserActivityLog;

public interface LogUserActivityUseCasePort {
    void logActivity(UserActivityLog log);
}

