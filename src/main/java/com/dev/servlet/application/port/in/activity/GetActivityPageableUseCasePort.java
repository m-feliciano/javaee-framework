package com.dev.servlet.application.port.in.activity;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

public interface GetActivityPageableUseCasePort {
    <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<UserActivityLog, U> mapper);
}

