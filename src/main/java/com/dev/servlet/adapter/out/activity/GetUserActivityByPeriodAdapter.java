package com.dev.servlet.adapter.out.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.activity.GetUserActivityByPeriodPort;
import com.dev.servlet.application.port.out.activity.UserActivityLogRepositoryPort;
import com.dev.servlet.domain.entity.UserActivityLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GetUserActivityByPeriodAdapter implements GetUserActivityByPeriodPort {

    @Inject
    private UserActivityLogRepositoryPort repositoryPort;

    @Override
    public <U> List<U> getByPeriod(UUID userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper) {
        log.debug("Fetching activity logs for user {} from {} to {}", userId, startDate, endDate);
        List<UserActivityLog> activities = repositoryPort.findByUserIdAndDateRange(userId, startDate, endDate, null);
        return activities.stream().map(mapper::map).toList();
    }
}
