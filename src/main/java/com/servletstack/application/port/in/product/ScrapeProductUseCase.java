package com.servletstack.application.port.in.product;

import com.servletstack.application.transfer.response.ProductResponse;

import java.util.List;

public interface ScrapeProductUseCase {
    List<ProductResponse> scrape(String url, String auth);
}
