package com.dev.servlet.application.port.in.category;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.response.CategoryResponse;

import java.util.UUID;

public interface GetCategoryDetailUseCase {
    CategoryResponse get(UUID uuid) throws AppException;
}

