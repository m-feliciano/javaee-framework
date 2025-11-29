package com.dev.servlet.infrastructure.external.webscrape;

import java.util.Optional;

public interface IWebScrapeService<T> {
    Optional<T> scrape(WebScrapeRequest request) throws Exception;
}
