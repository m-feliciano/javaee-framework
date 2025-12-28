package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.ProductRequest;

public interface DeleteProductUseCase {
    void delete(ProductRequest request, String auth) throws AppException;
}
