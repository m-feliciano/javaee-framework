package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;

public interface ProductDetailPort {
    ProductResponse get(ProductRequest request, String auth) throws AppException;
}

