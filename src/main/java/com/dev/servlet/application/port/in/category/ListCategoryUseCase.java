package com.dev.servlet.application.port.in.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;

import java.util.Collection;

public interface ListCategoryUseCase {
    Collection<CategoryResponse> list(CategoryRequest category, String auth) throws AppException;
}

