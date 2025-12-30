package com.servletstack.application.port.out.activity;

import com.servletstack.application.port.out.PageablePort;
import com.servletstack.domain.entity.UserActivityLog;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserActivityLogRepositoryPort extends PageablePort<UserActivityLog> {
    Collection<UserActivityLog> findAll(UserActivityLog log);

    List<UserActivityLog> findByUserIdAndDateRange(UUID userId, Date startDate, Date endDate, String status);

    UserActivityLog save(UserActivityLog log);

    Optional<UserActivityLog> findById(UUID id);
}

