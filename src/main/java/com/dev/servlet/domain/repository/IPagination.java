package com.dev.servlet.domain.repository;
import com.dev.servlet.core.mapper.Mapper;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.IPageable;

/**
 * Interface for providing pagination capabilities to repositories.
 * 
 * <p>This interface defines methods for retrieving paginated data from the data store,
 * supporting both direct entity pagination and transformed data pagination using mappers.
 * It enables efficient handling of large datasets by allowing retrieval of data in
 * smaller, manageable chunks.</p>
 * 
 * @param <TData> the type of data this pagination interface handles
 * @author servlets-team
 * @since 1.0
 */
public interface IPagination<TData> {
    IPageable<TData> getAllPageable(IPageRequest pageRequest);

    <TMapper> IPageable<TMapper> getAllPageable(IPageRequest pageRequest, Mapper<TData, TMapper> mapper);
}
