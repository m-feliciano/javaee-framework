package com.servletstack.adapter.out.external.webscrape;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public record WebScrapeService<T>(WebScrapeServiceRegistry registry) {
    public Optional<T> execute(WebScrapeRequest request, Consumer<Exception> exceptionHandler) {
        try {
            @SuppressWarnings("unchecked")
            var service = (IWebScrapeService<T>) registry.getService(request.serviceType());
            Objects.requireNonNull(service, "Service not found for type: " + request.serviceType());
            return service.scrape(request);
        } catch (Exception e) {
            if (exceptionHandler == null) throw new RuntimeException(e);
            exceptionHandler.accept(e);
        }
        return Optional.empty();
    }
}
