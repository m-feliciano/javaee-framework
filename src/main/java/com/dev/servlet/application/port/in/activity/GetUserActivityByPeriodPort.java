package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.UserActivityLog;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface GetUserActivityByPeriodPort {
    <U> List<U> getByPeriod(UUID userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper);
}

