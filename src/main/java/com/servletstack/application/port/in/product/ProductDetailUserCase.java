package com.servletstack.application.port.in.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;

public interface ProductDetailUserCase {
    ProductResponse get(ProductRequest request, String auth) throws AppException;
}

