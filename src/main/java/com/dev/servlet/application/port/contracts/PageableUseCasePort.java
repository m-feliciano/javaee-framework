package com.dev.servlet.application.port.contracts;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;

public interface PageableUseCasePort<T> {
    <R> IPageable<R> getAllPageable(IPageRequest pageRequest, Mapper<T, R> mapper);
}
