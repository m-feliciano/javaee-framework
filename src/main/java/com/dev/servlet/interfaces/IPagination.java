package com.dev.servlet.interfaces;

import com.dev.servlet.pojo.records.Pagination;

import java.util.Collection;

/**
 * This interface is used to implement query in the application.
 *
 * @param <T> {@linkplain T} is the object
 * @param <K> {@linkplain K} is the key
 */
public interface IPagination<T, K> {

    /**
     * Find all results by filter
     *
     * @param object {@linkplain T}
     * @return a collection of {@linkplain T}
     */
    Collection<K> findAllOnlyIds(T object);

    /**
     * Get all results with pagination
     *
     * @param ids a collection of {@linkplain K}
     * @return a collection of {@linkplain T}
     */
    Collection<T> getAllPageable(Collection<K> ids, Pagination pagination);
}
