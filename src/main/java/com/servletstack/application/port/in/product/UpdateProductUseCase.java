package com.servletstack.application.port.in.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;

public interface UpdateProductUseCase {
    ProductResponse update(ProductRequest request, String auth) throws AppException;
}

