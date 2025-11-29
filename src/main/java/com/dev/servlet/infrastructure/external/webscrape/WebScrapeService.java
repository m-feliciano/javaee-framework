package com.dev.servlet.infrastructure.external.webscrape;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public record WebScrapeService<T>(WebScrapeServiceRegistry registry) {

    public Optional<T> execute(WebScrapeRequest request, Consumer<Exception> exceptionHandler) throws Exception {
        try {
            var service = (IWebScrapeService<T>) registry.getService(request.getServiceType());
            Objects.requireNonNull(service, "Service not found for type: " + request.getServiceType());
            return service.scrape(request);

        } catch (Exception e) {
            if (exceptionHandler == null) throw e;
            exceptionHandler.accept(e);
        }
        return Optional.empty();
    }
}
