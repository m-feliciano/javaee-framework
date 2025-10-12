package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.transfer.response.CategoryResponse;
import com.dev.servlet.domain.transfer.request.CategoryRequest;

import java.util.Collection;

/**
 * Service interface for managing category operations in the servlet application.
 * 
 * <p>This interface defines the contract for all category-related business operations,
 * including category management, hierarchical operations, and product categorization.
 * Categories are used to organize and classify products within the application,
 * providing a structured way to manage product taxonomy.</p>
 * 
 * @author servlets-team
 * @since 1.0
 */
public interface ICategoryService {
    CategoryResponse register(CategoryRequest category, String auth) throws ServiceException;

    CategoryResponse update(CategoryRequest category, String auth) throws ServiceException;

    CategoryResponse getById(CategoryRequest category, String auth) throws ServiceException;

    Collection<CategoryResponse> list(CategoryRequest category, String auth) throws ServiceException;

    void delete(CategoryRequest category, String auth) throws ServiceException;
}
