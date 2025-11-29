package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.port.in.activity.GetUserActivityDetailUseCasePort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.repository.UserActivityLogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class GetUserActivityDetailUseCase implements GetUserActivityDetailUseCasePort {

    @Inject
    private UserActivityLogRepository repository;

    @Override
    public Optional<UserActivityLog> getActivityDetail(String activityId, String userId) {
        log.debug("Fetching activity log {} for user {}", activityId, userId);
        Optional<UserActivityLog> activityLog = repository.findById(activityId);
        if (activityLog.isPresent() && !activityLog.get().getUserId().equals(userId)) {
            log.warn("User {} attempted to access activity log {} belonging to another user", userId, activityId);
            return Optional.empty();
        }

        return activityLog;
    }
}

