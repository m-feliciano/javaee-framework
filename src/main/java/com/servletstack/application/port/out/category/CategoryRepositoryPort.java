package com.servletstack.application.port.out.category;

import com.servletstack.domain.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    List<Category> findAll(Category category);

    Optional<Category> find(Category category);

    void delete(Category category);

    List<Category> saveAll(List<Category> categories);

    void updateName(Category category);

    Category save(Category category);
}
