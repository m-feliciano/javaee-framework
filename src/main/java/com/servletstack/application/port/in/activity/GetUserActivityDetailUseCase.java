package com.servletstack.application.port.in.activity;

import com.servletstack.domain.entity.UserActivityLog;

import java.util.Optional;
import java.util.UUID;

public interface GetUserActivityDetailUseCase {
    Optional<UserActivityLog> getActivityDetail(UUID activityId, UUID userId);
}

