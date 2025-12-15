package com.dev.servlet.adapter.out.external.webscrape.service;

import com.dev.servlet.adapter.out.external.webscrape.WebScrapeRequest;
import com.dev.servlet.adapter.out.external.webscrape.api.ScrapeApiClient;
import com.dev.servlet.adapter.out.external.webscrape.transfer.ProductWebScrapeDTO;
import com.dev.servlet.adapter.out.external.webscrape.transfer.WebScrapingResponse;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.infrastructure.utils.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ProductWebScrapeApiClient extends ScrapeApiClient<List<ProductWebScrapeDTO>> {

    @Override
    public Optional<List<ProductWebScrapeDTO>> scrape(WebScrapeRequest request) throws Exception {
        try {
            final Path tempFile = FileUtil.readResourceFile("/mock/product/webscrape.json");
            String content = Files.readString(tempFile);
            List<ProductWebScrapeDTO> response = objectMapper.readValue(content, new TypeReference<>() {
            });

            // Uncomment below to enable live scraping
//            List<ProductWebScrapeDTO> response = fetchProductsFromScrapingApi(scrapeRequest).stream()
//                    .flatMap(r -> r.getContent().stream())
//                    .collect(Collectors.toList());

            log.debug("[Scraped] Loaded {} products from cached file: {}", response.size(), tempFile);
            return Optional.of(response);

        } catch (Exception e) {
            log.error("Error scraping products: {}", e.getMessage(), e);
            throw new AppException("Error scraping products. See logs for details.");
        }
    }

    private List<WebScrapingResponse<ProductWebScrapeDTO>> fetchProductsFromScrapingApi(WebScrapeRequest scrapeRequest) throws AppException {
        if (scrapeRequest == null || scrapeRequest.url() == null) {
            throw new AppException("Scrape request or URL cannot be null.");
        }
        List<WebScrapingResponse<ProductWebScrapeDTO>> responses = new ArrayList<>();
        WebScrapingResponse<ProductWebScrapeDTO> scrapingResponse;
        int page = 1;
        int pageTotal;
        int MAX_PAGES = 50;
        do {
            String url = scrapeRequest.url().replace("<page>", String.valueOf(page));

            Request request = new Request.Builder().url(url).get().build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("Error retrieving page {}: {}", page, response.message());
                    throw new AppException(response.message());
                }

                String responseBody = response.body().string();
                scrapingResponse = objectMapper.readValue(responseBody,
                        new TypeReference<WebScrapingResponse<ProductWebScrapeDTO>>() {
                        });

                responses.add(scrapingResponse);
                pageTotal = scrapingResponse.getPageTotal();

            } catch (Exception e) {
                throw new AppException("Error fetching page " + page + ": " + e.getMessage());
            }
            page++;

        } while (page < pageTotal && page <= MAX_PAGES);

        if (page > MAX_PAGES) {
            log.warn("Maximum number of pages reached: {}. Stopping scraping.", MAX_PAGES);
        }

        return responses;
    }
}
