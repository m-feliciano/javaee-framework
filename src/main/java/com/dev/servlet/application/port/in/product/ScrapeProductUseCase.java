package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.transfer.response.ProductResponse;

import java.util.List;

public interface ScrapeProductUseCase {
    List<ProductResponse> scrape(String url, String auth);
}
