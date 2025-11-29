package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.activity.GetActivityPageableUseCasePort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.repository.UserActivityLogRepository;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class GetActivityPageableUseCase implements GetActivityPageableUseCasePort {

    @Inject
    private UserActivityLogRepository repository;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<UserActivityLog, U> mapper) {
        log.debug("Fetching pageable activity logs: page {}, size {}", pageRequest.getInitialPage(), pageRequest.getPageSize());
        return repository.getAllPageable(pageRequest, mapper);
    }
}
