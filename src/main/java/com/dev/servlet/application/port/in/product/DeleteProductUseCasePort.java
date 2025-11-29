package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.ProductRequest;

public interface DeleteProductUseCasePort {
    void delete(ProductRequest request, String auth) throws ApplicationException;
}
