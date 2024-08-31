package com.dev.servlet.interfaces;

import com.dev.servlet.pojo.records.Pagable;

import java.util.Collection;

/**
 * This interface is used to implement pagination in the application.
 *
 * @param <T>
 */
public interface IPagination<T> {

    Long getTotalResults(T object);

    Collection<T> findAll(T object, Pagable pagable);

}
