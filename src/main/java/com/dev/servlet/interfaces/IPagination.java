package com.dev.servlet.interfaces;

import com.dev.servlet.pojo.records.Pagination;

import java.util.Collection;

/**
 * This interface is used to implement query in the application.
 *
 * @param <T>
 */
public interface IPagination<T> {

    Long getTotalResults(T object);

    Collection<T> findAll(T object, Pagination pagination);

}
