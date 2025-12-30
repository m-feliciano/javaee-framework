package com.servletstack.application.port.in.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.ProductRequest;

public interface DeleteProductUseCase {
    void delete(ProductRequest request, String auth) throws AppException;
}
