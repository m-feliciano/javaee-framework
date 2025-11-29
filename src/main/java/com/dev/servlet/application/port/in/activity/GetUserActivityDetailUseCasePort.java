package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.domain.entity.UserActivityLog;

import java.util.Optional;

public interface GetUserActivityDetailUseCasePort {
    Optional<UserActivityLog> getActivityDetail(String activityId, String userId);
}

