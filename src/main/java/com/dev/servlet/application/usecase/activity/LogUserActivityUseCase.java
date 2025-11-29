package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.port.in.activity.LogUserActivityUseCasePort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.repository.UserActivityLogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class LogUserActivityUseCase implements LogUserActivityUseCasePort {

    @Inject
    private UserActivityLogRepository repository;

    @Override
    public void logActivity(UserActivityLog ual) {
        repository.save(ual);
    }
}
