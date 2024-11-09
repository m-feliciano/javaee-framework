package com.dev.servlet.interfaces;

import java.util.List;

public interface IController<T, E> {
    default T findById(E id) {
        return null;
    }

    T find(T object);

    List<T> findAll(T object);

    void save(T object);

    T update(T object);

    void delete(T object);
}
