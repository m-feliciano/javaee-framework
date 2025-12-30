package com.servletstack.application.port.out;

import com.servletstack.application.mapper.Mapper;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

public interface PageablePort<T> {
    <R> IPageable<R> getAllPageable(IPageRequest pageRequest, Mapper<T, R> mapper);
}
