package com.servletstack.infrastructure.persistence.repository.base;

import com.servletstack.application.port.out.PageablePort;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.IPageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BaseRepositoryPort<T, ID> extends PageablePort<T>, Serializable {

    Optional<T> findById(ID id);

    Optional<T> find(T filter);

    Collection<T> findAll(T filter);

    IPageable<T> getAllPageable(IPageRequest pageRequest);

    long count(IPageRequest pageRequest);

    T save(T entity);

    List<T> saveAll(List<T> entities);

    T update(T entity);

    void delete(T entity);
}
