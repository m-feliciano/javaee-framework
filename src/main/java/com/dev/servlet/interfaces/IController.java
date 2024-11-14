package com.dev.servlet.interfaces;

import java.util.Collection;

public interface IController<T, E> {

    T findById(E id);

    T find(T object);

    Collection<T> findAll(T object);

    void save(T object);

    T update(T object);

    void delete(T object);
}
