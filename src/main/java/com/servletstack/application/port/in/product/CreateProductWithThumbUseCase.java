package com.servletstack.application.port.in.product;

import com.servletstack.application.transfer.request.ProductRequest;
import com.servletstack.application.transfer.response.ProductResponse;

public interface CreateProductWithThumbUseCase {

    ProductResponse execute(ProductRequest req, String auth);
}
