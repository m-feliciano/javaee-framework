package com.dev.servlet.domain.service;

import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;

import java.util.Optional;

public interface UserActivityService {

    void logActivity(UserActivityLog log);

    Optional<UserActivityLog> getActivityDetail(String activityId, String userId);

    <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<UserActivityLog, U> mapper);
}

