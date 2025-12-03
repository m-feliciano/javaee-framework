package com.dev.servlet.application.usecase.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.application.port.in.activity.GetActivityPageablePort;
import com.dev.servlet.application.port.out.activity.UserActivityLogRepositoryPort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class GetActivityPageableUseCase implements GetActivityPageablePort {

    @Inject
    private UserActivityLogRepositoryPort repositoryPort;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<UserActivityLog, U> mapper) {
        log.debug("Fetching pageable activity logs: page {}, size {}", pageRequest.getInitialPage(), pageRequest.getPageSize());
        return repositoryPort.getAllPageable(pageRequest, mapper);
    }
}
