package com.dev.servlet.adapter.out.external.webscrape.builder;

import com.dev.servlet.adapter.out.external.webscrape.WebScrapeRequest;
import com.dev.servlet.adapter.out.external.webscrape.WebScrapeService;
import com.dev.servlet.adapter.out.external.webscrape.WebScrapeServiceRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebScrapeBuilder<T> {
    private String serviceType;
    private String url;
    private Map<String, Object> params;
    private WebScrapeServiceRegistry registry;
    private Consumer<Exception> exceptionHandler;

    public static <T> WebScrapeBuilder<T> create() {
        return new WebScrapeBuilder<>();
    }

    public WebScrapeBuilder<T> withServiceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public WebScrapeBuilder<T> withUrl(String url) {
        this.url = url;
        return this;
    }

    public WebScrapeBuilder<T> withParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public WebScrapeBuilder<T> withRegistry(WebScrapeServiceRegistry registry) {
        this.registry = registry;
        return this;
    }

    public WebScrapeBuilder<T> onErrorHandler(Consumer<Exception> handler) {
        this.exceptionHandler = handler;
        return this;
    }

    public Optional<T> execute() {
        Objects.requireNonNull(registry, "WebScrapeServiceRegistry must be set");
        Objects.requireNonNull(serviceType, "WebScrapeServiceType must be set");
        Objects.requireNonNull(url, "WebScrape URL must be set");
        WebScrapeRequest request = new WebScrapeRequest(serviceType, url, params);
        WebScrapeService<T> service = new WebScrapeService<>(registry);
        return service.execute(request, exceptionHandler);
    }
}
