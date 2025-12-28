package com.dev.servlet.application.port.in.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;

public interface UpdateCategoryUseCase {
    CategoryResponse update(CategoryRequest category, String auth) throws AppException;
}