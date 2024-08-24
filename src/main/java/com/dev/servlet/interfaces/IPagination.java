package com.dev.servlet.interfaces;

import java.util.Collection;

/**
 * This interface is used to implement pagination in the application.
 *
 * @param <T>
 */
public interface IPagination<T> {

    Long getTotalResults(T object);

    Collection<T> findAll(T object, int first, int pageSize);

}
