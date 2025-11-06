package com.dev.servlet.domain.repository;
import java.util.Collection;
import java.util.Optional;

public interface ICrudRepository<T, ID> {
    Optional<T> findById(ID id);
    Optional<T> find(T object);
    Collection<T> findAll(T object);
    T save(T object);
    T update(T object);
    boolean delete(T object);
}
