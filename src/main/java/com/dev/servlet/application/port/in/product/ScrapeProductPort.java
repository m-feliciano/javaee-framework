package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.transfer.response.ProductResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ScrapeProductPort {
    CompletableFuture<List<ProductResponse>> scrapeAsync(String url, String environment, String auth);
}
