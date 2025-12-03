package com.dev.servlet.application.port.in.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.CategoryRequest;
import com.dev.servlet.application.transfer.response.CategoryResponse;

public interface UpdateCategoryPort {
    CategoryResponse update(CategoryRequest category, String auth) throws ApplicationException;
}