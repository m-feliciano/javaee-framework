package com.dev.servlet.application.port.in.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.CategoryRequest;

public interface DeleteCategoryUseCase {
    void delete(CategoryRequest category, String auth) throws AppException;
}
