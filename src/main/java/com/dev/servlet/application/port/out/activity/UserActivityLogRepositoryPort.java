package com.dev.servlet.application.port.out.activity;

import com.dev.servlet.application.port.out.PageablePort;
import com.dev.servlet.domain.entity.UserActivityLog;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserActivityLogRepositoryPort extends PageablePort<UserActivityLog> {
    Collection<UserActivityLog> findAll(UserActivityLog log);

    List<UserActivityLog> findByUserIdAndDateRange(String userId, Date startDate, Date endDate, String status);

    UserActivityLog save(UserActivityLog log);

    Optional<UserActivityLog> findById(String id);
}

