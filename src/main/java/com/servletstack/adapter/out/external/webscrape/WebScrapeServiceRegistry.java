package com.servletstack.adapter.out.external.webscrape;

import com.servletstack.adapter.out.external.webscrape.service.ProductWebScrapeApiClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WebScrapeServiceRegistry {
    private final Map<String, IWebScrapeService<?>> registry = new ConcurrentHashMap<>();

    public WebScrapeServiceRegistry() {
        registerService("product", new ProductWebScrapeApiClient());
    }

    public IWebScrapeService<?> getService(String type) {
        return registry.get(type);
    }

    private void registerService(String type, IWebScrapeService<?> service) {
        registry.put(type, service);
    }
}
