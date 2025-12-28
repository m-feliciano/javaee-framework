package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.exception.NotFoundException;
import com.dev.servlet.application.port.in.activity.GetUserActivityDetailUseCase;
import com.dev.servlet.application.port.out.activity.UserActivityLogRepositoryPort;
import com.dev.servlet.domain.entity.UserActivityLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class GetUserActivityDetailService implements GetUserActivityDetailUseCase {

    @Inject
    private UserActivityLogRepositoryPort repository;

    @Override
    public Optional<UserActivityLog> getActivityDetail(UUID activityId, UUID userId) {
        log.debug("Fetching activity log {} for user {}", activityId, userId);
        Optional<UserActivityLog> activityLog = repository.findById(activityId);
        if (activityLog.isPresent() && !activityLog.get().getUserId().equals(userId)) {
            log.warn("User {} attempted to access activity log {} belonging to another user", userId, activityId);
            throw new NotFoundException();
        }

        return activityLog;
    }
}

