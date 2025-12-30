package com.servletstack.adapter.out.activity;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.application.port.in.activity.GetActivityPageableUseCase;
import com.servletstack.application.port.out.activity.UserActivityLogRepositoryPort;
import com.servletstack.domain.entity.UserActivityLog;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class GetActivityPageableAdapter implements GetActivityPageableUseCase {

    @Inject
    private UserActivityLogRepositoryPort repository;

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<UserActivityLog, U> mapper) {
        log.debug("Fetching pageable activity logs: page {}, size {}", pageRequest.getInitialPage(), pageRequest.getPageSize());
        return repository.getAllPageable(pageRequest, mapper);
    }
}
