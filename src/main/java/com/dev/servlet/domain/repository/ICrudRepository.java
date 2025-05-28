package com.dev.servlet.domain.repository;
import java.util.Collection;

public interface ICrudRepository<T, ID> extends IPagination<T> {
    T findById(ID id);
    T find(T object);
    Collection<T> findAll(T object);
    T save(T object);
    T update(T object);
    boolean delete(T object);
}
