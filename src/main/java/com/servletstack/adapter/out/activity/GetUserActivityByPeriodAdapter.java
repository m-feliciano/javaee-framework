package com.servletstack.adapter.out.activity;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.application.port.in.activity.GetUserActivityByPeriodUseCase;
import com.servletstack.application.port.out.activity.UserActivityLogRepositoryPort;
import com.servletstack.domain.entity.UserActivityLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GetUserActivityByPeriodAdapter implements GetUserActivityByPeriodUseCase {

    @Inject
    private UserActivityLogRepositoryPort repository;

    @Override
    public <U> List<U> getByPeriod(UUID userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper) {
        log.debug("Fetching activity logs for user {} from {} to {}", userId, startDate, endDate);
        List<UserActivityLog> activities = repository.findByUserIdAndDateRange(userId, startDate, endDate, null);
        return activities.stream().map(mapper::map).toList();
    }
}
