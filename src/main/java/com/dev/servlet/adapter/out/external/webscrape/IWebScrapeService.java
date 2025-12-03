package com.dev.servlet.adapter.out.external.webscrape;

import java.util.Optional;

public interface IWebScrapeService<T> {
    Optional<T> scrape(WebScrapeRequest request) throws Exception;
}
