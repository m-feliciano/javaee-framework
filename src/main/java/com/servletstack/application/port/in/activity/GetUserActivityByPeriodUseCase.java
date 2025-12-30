package com.servletstack.application.port.in.activity;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.domain.entity.UserActivityLog;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface GetUserActivityByPeriodUseCase {
    <U> List<U> getByPeriod(UUID userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper);
}

