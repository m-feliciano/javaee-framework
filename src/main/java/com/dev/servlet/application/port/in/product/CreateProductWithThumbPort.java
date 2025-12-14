package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;

public interface CreateProductWithThumbPort {

    ProductResponse execute(ProductRequest req, String auth);
}
