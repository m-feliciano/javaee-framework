package com.dev.servlet.application.port.in.category;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.CategoryRequest;

public interface DeleteCategoryUseCasePort {
    void delete(CategoryRequest category, String auth) throws ApplicationException;
}
