package com.servletstack.application.port.in.category;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.CategoryRequest;

public interface DeleteCategoryUseCase {
    void delete(CategoryRequest category, String auth) throws AppException;
}
