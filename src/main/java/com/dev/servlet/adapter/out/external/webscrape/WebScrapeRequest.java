package com.dev.servlet.adapter.out.external.webscrape;

import java.util.Map;

public record WebScrapeRequest(String serviceType, String url, Map<String, Object> params) {
}
