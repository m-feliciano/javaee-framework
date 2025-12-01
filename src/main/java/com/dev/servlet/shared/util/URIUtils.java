package com.dev.servlet.shared.util;

import com.dev.servlet.domain.valueobject.KeyPair;
import com.dev.servlet.domain.valueobject.Query;
import com.dev.servlet.domain.valueobject.Sort;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class URIUtils {
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final String DEFAULT_SORT_ORDER = "desc";
    private static final int DEFAULT_MIN_PAGE_SIZE = 1;
    private static final int DEFAULT_INITIAL_PAGE = 1;
    private static final String PAGINATION_PAGE = "pagination.page";
    private static final String PAGINATION_LIMIT = "pagination.limit";
    private static final String PAGINATION_SORT = "pagination.sort";
    private static final String PAGINATION_ORDER = "pagination.order";

    public static String getResourceId(HttpServletRequest httpServletRequest) {
        String[] array = httpServletRequest.getServletPath().split("/");
        return Arrays.stream(array).skip(5).findFirst().orElse(null);
    }

    public static IPageRequest getPageRequest(HttpServletRequest request) {
        IPageRequest pageRequest;
        if (hasQueryString(request)) {
            Map<String, String> queryParams = filterQueryParameters(request);
            pageRequest = createPageRequest(queryParams);
        } else {
            pageRequest = buildPagination();
        }
        return pageRequest;
    }

    private static boolean hasQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString != null && !queryString.isEmpty();
    }

    private static Map<String, String> filterQueryParameters(HttpServletRequest request) {
        Map<String, String> queryParams = new HashMap<>();
        List<KeyPair> params = parseQueryParams(request.getQueryString());
        for (var param : params) {
            queryParams.put(param.getKey(), URLDecoder.decode(((String) param.value()).trim(), StandardCharsets.UTF_8));
        }
        return queryParams;
    }

    private static IPageRequest createPageRequest(Map<String, String> queryParams) {
        int pageInitial = parsePageNumber(queryParams);
        int pageSize = parsePageSize(queryParams);
        Sort sort = createSort(queryParams);
        return PageRequest.builder().initialPage(pageInitial).pageSize(pageSize).sort(sort).build();
    }

    private static int parsePageNumber(Map<String, String> queryParams) {
        try {
            int page = Integer.parseInt(queryParams.getOrDefault("page", String.valueOf(DEFAULT_INITIAL_PAGE)));
            return Math.max(Math.abs(page), DEFAULT_INITIAL_PAGE);
        } catch (NumberFormatException e) {
            return DEFAULT_INITIAL_PAGE;
        }
    }

    private static int parsePageSize(Map<String, String> queryParams) {
        try {
            String parameter = queryParams.getOrDefault("limit", String.valueOf(DEFAULT_MIN_PAGE_SIZE));
            int limit = Math.abs(Integer.parseInt(parameter));
            return Math.max(limit, DEFAULT_MIN_PAGE_SIZE);
        } catch (NumberFormatException e) {
            return DEFAULT_MIN_PAGE_SIZE;
        }
    }

    private static Sort createSort(Map<String, String> queryParams) {
        String sortField = queryParams.getOrDefault("sort", DEFAULT_SORT_FIELD);
        String order = queryParams.getOrDefault("order", DEFAULT_SORT_ORDER);
        return Sort.by(sortField).direction(Sort.Direction.from(order));
    }

    private static List<KeyPair> parseQueryParams(String query) {
        List<KeyPair> queryParams = new ArrayList<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                queryParams.add(new KeyPair(pair[0], pair[1]));
            }
        }
        return queryParams;
    }

    private static PageRequest buildPagination() {
        int page = Properties.getOrDefault(PAGINATION_PAGE, DEFAULT_INITIAL_PAGE);
        int size = Properties.getOrDefault(PAGINATION_LIMIT, DEFAULT_MIN_PAGE_SIZE);
        String field = Properties.getOrDefault(PAGINATION_SORT, DEFAULT_SORT_FIELD);
        String order = Properties.getOrDefault(PAGINATION_ORDER, DEFAULT_SORT_ORDER);
        Sort sort = Sort.by(field).direction(Sort.Direction.from(order));
        return PageRequest.builder().initialPage(page).pageSize(size).sort(sort).build();
    }

    public static List<KeyPair> getParameters(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameterMap()
                .entrySet().stream()
                .filter(r -> !r.getKey().startsWith("X-"))
                .map(e -> new KeyPair(e.getKey(), e.getValue()[0]))
                .collect(Collectors.toList());
    }

    public static String getErrorMessage(int status) {
        return switch (status) {
            case HttpServletResponse.SC_BAD_REQUEST -> "Bad Request";
            case HttpServletResponse.SC_UNAUTHORIZED -> "Unauthorized";
            case HttpServletResponse.SC_SERVICE_UNAVAILABLE -> "Service Unavailable";
            case HttpServletResponse.SC_FORBIDDEN -> "Forbidden";
            case HttpServletResponse.SC_NOT_FOUND -> "Not Found";
            case HttpServletResponse.SC_METHOD_NOT_ALLOWED -> "Method Not Allowed";
            case HttpServletResponse.SC_CONFLICT -> "Conflict";
            case 429 -> "Too Many Requests";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "Error";
        };
    }

    public static Query query(HttpServletRequest servletRequest) {
        if (!hasQueryString(servletRequest)) return null;

        Map<String, String> queries = filterQueryParameters(servletRequest);
        String field = queries.remove("k");
        String value = queries.remove("q");
        if (field != null && value != null) {
            queries.put(field, value);
        }

        return new Query(queries);
    }
}
