package com.dev.servlet.domain.repository;
import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;

public interface IPagination<TData> {
    IPageable<TData> getAllPageable(IPageRequest pageRequest);

    <TMapper> IPageable<TMapper> getAllPageable(IPageRequest pageRequest, Mapper<TData, TMapper> mapper);
}
