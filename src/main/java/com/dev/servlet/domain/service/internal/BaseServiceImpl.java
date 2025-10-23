package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.repository.ICrudRepository;
import com.dev.servlet.domain.repository.IPagination;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;
import com.dev.servlet.infrastructure.persistence.dao.base.BaseDAO;
import com.dev.servlet.infrastructure.persistence.internal.PageResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
public abstract class BaseServiceImpl<T, ID> implements ICrudRepository<T, ID>, IPagination<T> {

    protected BaseDAO<T, ID> baseDAO;
    protected BaseServiceImpl(BaseDAO<T, ID> baseDAO) {
        this.baseDAO = baseDAO;
    }

    protected JwtUtil jwts;

    @Inject
    public void setJwts(JwtUtil jwts) {
        this.jwts = jwts;
    }

    @Override
    public Collection<T> findAll(T object) {
        return baseDAO.findAll(object);
    }

    @Override
    public Optional<T> find(T filter) {
        return baseDAO.find(filter);
    }

    @Override
    public Optional<T> findById(ID id) {
        return baseDAO.findById(id);
    }

    @Override
    public T save(T object) {
        return baseDAO.save(object);
    }

    @Override
    public T update(T object) {
        return baseDAO.update(object);
    }

    @Override
    public boolean delete(T object) {
        return baseDAO.delete(object);
    }

    @Override
    public IPageable<T> getAllPageable(IPageRequest pageRequest) {
        long totalCount = baseDAO.count(pageRequest);
        List<T> resultSet = Collections.emptyList();
        if (totalCount > pageRequest.getFirstResult()) {
            resultSet = baseDAO.getAllPageable(pageRequest);
        }
        return PageResponse.<T>builder()
                .content(resultSet)
                .totalElements(totalCount)
                .currentPage(pageRequest.getInitialPage())
                .pageSize(pageRequest.getPageSize())
                .sort(pageRequest.getSort())
                .build();
    }

    @Override
    public <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<T, U> mapper) {
        IPageable<T> page = getAllPageable(pageRequest);
        var content = page.getContent().stream().map(mapper::map).toList();
        return PageResponse.<U>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .currentPage(page.getCurrentPage())
                .pageSize(page.getPageSize())
                .sort(page.getSort())
                .build();
    }
}
