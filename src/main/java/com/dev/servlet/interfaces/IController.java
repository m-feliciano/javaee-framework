package com.dev.servlet.interfaces;

import java.util.List;

public interface IController<T> {

	T findById(Long id);

	void save(T object);

	void update(T object);

	void delete(T object);

	List<T> findAll(T object);
}
