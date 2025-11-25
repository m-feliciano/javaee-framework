package com.dev.servlet.infrastructure.external.webscrape.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.infrastructure.external.webscrape.WebScrapeRequest;
import com.dev.servlet.infrastructure.external.webscrape.api.ScrapeApiClient;
import com.dev.servlet.infrastructure.external.webscrape.data.VirtualFileUtils;
import com.dev.servlet.infrastructure.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.infrastructure.external.webscrape.transfer.WebScrapingResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ProductWebScrapeApiClient extends ScrapeApiClient<List<ProductWebScrapeDTO>> {

    public static final String WEB_SCRAPE_JSON_FILENAME = "product-web-scrape.json";

    @Override
    public Optional<List<ProductWebScrapeDTO>> scrape(WebScrapeRequest scrapeRequest) throws Exception {
        try {
            final Path tempFile = VirtualFileUtils.readTempFile(WEB_SCRAPE_JSON_FILENAME);
            if (tempFile != null) {
                String content = Files.readString(tempFile);
                List<ProductWebScrapeDTO> response = objectMapper.readValue(content, new TypeReference<>() {
                });
                log.debug("[Scraped] Loaded {} products from cached file: {}", response.size(), tempFile);
                return Optional.of(response);
            }

            List<ProductWebScrapeDTO> response = fetchProductsFromScrapingApi(scrapeRequest).stream()
                    .flatMap(r -> r.getContent().stream())
                    .collect(Collectors.toList());

            if (response.isEmpty()) return Optional.empty();

            String json = CloneUtil.toJson(response);
            final Path output = VirtualFileUtils.createTempFile(json, WEB_SCRAPE_JSON_FILENAME);
            log.debug("[Scraped] {} products. Data saved to temporary file: {}", response.size(), output);
            return Optional.of(response);

        } catch (Exception e) {
            log.error("Error scraping products: {}", e.getMessage(), e);
            throw new ServiceException("Error scraping products. See logs for details.");
        }
    }

    private List<WebScrapingResponse<ProductWebScrapeDTO>> fetchProductsFromScrapingApi(WebScrapeRequest scrapeRequest) throws ServiceException {
        if (scrapeRequest == null || scrapeRequest.getUrl() == null) {
            throw new ServiceException("Scrape request or URL cannot be null.");
        }
        List<WebScrapingResponse<ProductWebScrapeDTO>> responses = new ArrayList<>();
        WebScrapingResponse<ProductWebScrapeDTO> scrapingResponse;
        int page = 1;
        int pageTotal;
        int MAX_PAGES = 50;
        do {
            String url = scrapeRequest.getUrl().replace("<page>", String.valueOf(page));
            Request request = new Request.Builder().url(url).get().build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("Error retrieving page {}: {}", page, response.message());
                    throw new ServiceException(response.message());
                }
                String responseBody = response.body().string();
                TypeReference<WebScrapingResponse<ProductWebScrapeDTO>> typeReference = new TypeReference<>() {
                };
                scrapingResponse = objectMapper.readValue(responseBody, typeReference);
                responses.add(scrapingResponse);
                pageTotal = scrapingResponse.getPageTotal();
            } catch (Exception e) {
                log.error("Error fetching page {}: {}", page, e.getMessage(), e);
                throw new ServiceException("Error fetching page " + page + ": " + e.getMessage());
            }
            page++;
        } while (page < pageTotal && page <= MAX_PAGES);
        if (page > MAX_PAGES) {
            log.warn("Maximum number of pages reached: {}. Stopping scraping.", MAX_PAGES);
        }
        return responses;
    }
}
