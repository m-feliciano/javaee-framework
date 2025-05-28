package com.dev.servlet.infrastructure.external.webscrape;
import java.util.Optional;

public interface IWebScrapeService<TResponse> {
    Optional<TResponse> scrape(WebScrapeRequest request) throws Exception;
}
