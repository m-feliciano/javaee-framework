package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.activity.GetUserActivityByPeriodUseCasePort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.repository.UserActivityLogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class GetUserActivityByPeriodUseCase implements GetUserActivityByPeriodUseCasePort {

    @Inject
    private UserActivityLogRepository repository;

    @Override
    public <U> List<U> getByPeriod(String userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper) {
        log.debug("Fetching activity logs for user {} from {} to {}", userId, startDate, endDate);
        List<UserActivityLog> activities = repository.findByUserIdAndDateRange(userId, startDate, endDate, null);
        return activities.stream().map(mapper::map).toList();
    }
}
