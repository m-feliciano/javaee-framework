package com.servletstack.application.port.in.category;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.CategoryRequest;
import com.servletstack.application.transfer.response.CategoryResponse;

public interface UpdateCategoryUseCase {
    CategoryResponse update(CategoryRequest category, String auth) throws AppException;
}