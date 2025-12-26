package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.domain.entity.UserActivityLog;

import java.util.Optional;
import java.util.UUID;

public interface GetUserActivityDetailPort {
    Optional<UserActivityLog> getActivityDetail(UUID activityId, UUID userId);
}

