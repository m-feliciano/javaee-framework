package com.dev.servlet.utils;

import com.dev.servlet.pojo.records.Order;
import com.dev.servlet.pojo.records.Pagination;
import com.dev.servlet.pojo.records.Query;
import com.dev.servlet.pojo.records.Sort;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class URIUtils {

    public static final String URI_INTERNAL_CACHE_KEY = "uri_internal_cache_key";

    private URIUtils() {
        // Empty constructor
    }

    /**
     * This action is used to get the service name from the request.
     *
     * @param request HttpServletRequest
     * @return service name
     */
    public static String service(HttpServletRequest request) {
        return extractURIPath(request, 1);
    }

    /**
     * Get the action from the request.
     *
     * @param request HttpServletRequest
     * @return action name
     */
    public static String action(HttpServletRequest request) {
        return extractURIPath(request, 2);
    }

    /**
     * Extracts a part of the URI based on the given index.
     * <p>
     * Obs: the uri must be in the format /view/{service}/{action}/{id|query} or /view/{service}/{action}
     * <p>
     * @param request HttpServletRequest
     * @param index   the index of the part to extract
     * @return the extracted part of the URI
     */
    private static String extractURIPath(HttpServletRequest request, int index) {
        String uri = request.getRequestURI();
        String[] uriParts = uri.split("/view/")[1].split("/");

        int correctIndex = index - 1;
        if (uriParts.length > correctIndex) {
            return uriParts[correctIndex];
        }

        return null;
    }


    /**
     * Return the path variable from the request.
     *
     * @param httpServletRequest
     * @return
     */
    public static Long recourceId(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getParameter("id"))
                .map(Long::parseLong)
                .orElseGet(() -> {
                    String[] uriParts = httpServletRequest.getRequestURI().split("/");
                    String urlID = uriParts[uriParts.length - 1];

                    Long id = urlID.matches("\\d+") ? Long.parseLong(urlID) : null;
                    return id;
                });
    }

    /**
     * Create the query object from the request.
     *
     * @param request
     * @return
     */
    public static Query query(HttpServletRequest request) {
        String search = null, type = null;
        Pagination pagination = getDefaultPageValue();

        if (request.getQueryString() != null) {
            int page = 1, size = 5;
            Sort sort = Sort.ID;
            Order order = Order.ASC;

            var entries = parseQueryParams(request.getQueryString()).entrySet();
            for (var entry : entries) {
                switch (entry.getKey()) {
                    case "page" -> page = Math.max(Math .abs(Integer.parseInt(entry.getValue())), 1);
                    case "limit" -> size = Math.min(Math .abs(Integer.parseInt(entry.getValue())), 50);
                    case "sort" -> sort = Sort.from(entry.getValue());
                    case "order" -> order = Order.from(entry.getValue());
                    case "q" -> search = URLDecoder.decode(entry.getValue().trim(), StandardCharsets.UTF_8);
                    case "k" -> type = entry.getValue();
                }
            }

            pagination = new Pagination(page, size, sort, order);
        }

        return new Query(pagination, search, type);
    }

    /**
     * Parse the query parameters from the request.
     *
     * @param query
     * @return
     */
    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                queryParams.put(pair[0], pair[1]);
            }
        }
        return queryParams;
    }

    /**
     * Get the default query value.
     * If the value is not found in the cache, then it will be created and stored in the cache.
     * The default values are read from the properties file.
     *
     * @return
     */
    private static Pagination getDefaultPageValue() {
        List<Object> objects = CacheUtil.get(URI_INTERNAL_CACHE_KEY, "default_pagination");

        if (!CollectionUtils.isNullOrEmpty(objects)) {
            return (Pagination) objects.get(0);
        }

        int page = Integer.parseInt(PropertiesUtil.getProperty("pagination.page", "1"));
        int size = Integer.parseInt(PropertiesUtil.getProperty("pagination.size", "5"));
        Sort sort = Sort.from(PropertiesUtil.getProperty("pagination.sort", "id"));
        Order order = Order.from(PropertiesUtil.getProperty("pagination.order", "asc"));

        Pagination pagination = new Pagination(page, size, sort, order);

        CacheUtil.set(URI_INTERNAL_CACHE_KEY, "default_pagination", List.of(pagination));

        return pagination;
    }
}
