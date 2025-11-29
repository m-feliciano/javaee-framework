package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.UserActivityLog;

import java.util.Date;
import java.util.List;

public interface GetUserActivityByPeriodUseCasePort {
    <U> List<U> getByPeriod(String userId, Date startDate, Date endDate, Mapper<UserActivityLog, U> mapper);
}

