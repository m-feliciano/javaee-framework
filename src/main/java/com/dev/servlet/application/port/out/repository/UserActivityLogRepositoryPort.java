package com.dev.servlet.application.port.out.repository;

import com.dev.servlet.domain.entity.UserActivityLog;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface UserActivityLogRepositoryPort {
    Collection<UserActivityLog> findAll(UserActivityLog object);

    List<UserActivityLog> findByUserIdAndDateRange(String userId, Date startDate, Date endDate, String status);
}

