package com.dev.servlet.infrastructure.external.webscrape;

import java.util.Map;

public record WebScrapeRequest(String serviceType, String url, Map<String, Object> params) {
}
