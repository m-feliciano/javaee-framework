package com.dev.servlet.application.port.out.repository;

import com.dev.servlet.domain.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    List<Category> findAll(Category category);

    Optional<Category> find(Category category);

    void delete(Category category);

    List<Category> save(List<Category> categories);
}
