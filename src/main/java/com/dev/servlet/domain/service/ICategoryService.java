package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.transfer.request.CategoryRequest;
import com.dev.servlet.domain.transfer.response.CategoryResponse;

import java.util.Collection;

public interface ICategoryService {
    CategoryResponse register(CategoryRequest category, String auth) throws ServiceException;

    CategoryResponse update(CategoryRequest category, String auth) throws ServiceException;

    CategoryResponse getById(CategoryRequest category, String auth) throws ServiceException;

    Collection<CategoryResponse> list(CategoryRequest category, String auth) throws ServiceException;

    void delete(CategoryRequest category, String auth) throws ServiceException;
}
