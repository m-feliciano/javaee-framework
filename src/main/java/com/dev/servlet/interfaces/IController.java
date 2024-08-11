package com.dev.servlet.interfaces;

import java.util.List;

public interface IController<T, E> {
    T findById(E id);

    T find(T object);

    List<T> findAll(T object);

    void save(T object);

    T update(T object);

    void delete(T object);
}
