package com.dev.servlet.adapter.out.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.activity.GetUserActivityByPeriodUseCase;
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
public class GetUserActivityByPeriodAdapter implements GetUserActivityByPeriodUseCase {

    @Inject
    private UserActivityLogRepositoryPort repository;

    @Override
    public <U> List<U> getByPeriod(UUID userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper) {
        return repository.findByUserIdAndDateRange(userId, startDate, endDate, null).stream()
                .map(mapper::map)
                .toList();
    }
}
