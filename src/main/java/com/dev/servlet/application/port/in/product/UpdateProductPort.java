package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;

public interface UpdateProductPort {
    ProductResponse update(ProductRequest request, String auth) throws ApplicationException;
}

