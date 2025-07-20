package com.dev.servlet.core.util;
import com.dev.servlet.domain.transfer.records.KeyPair;
import com.dev.servlet.domain.transfer.records.Query;
import com.dev.servlet.domain.transfer.records.Sort;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.dev.servlet.infrastructure.persistence.internal.PageRequest;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class URIUtils {
    public static final String URI_INTERNAL_CACHE_KEY = "uri_internal_cache_key";
    public static final String URI_INTERNAL_CACHE_VALUE_KEY = RandomStringUtils.randomAlphanumeric(30);
    public static final String DEFAULT_SORT_FIELD = "id";
    public static final String DEFAULT_SORT_ORDER = "asc";
    public static final int DEFAULT_MIN_PAGE_SIZE = 1;
    public static final int DEFAULT_INITIAL_PAGE = 1;

    public static String getResourceId(HttpServletRequest httpServletRequest) {
        String parameter = httpServletRequest.getParameter("id");
        if (parameter != null) return parameter;
        String[] array = httpServletRequest.getServletPath().split("/");
        parameter = Arrays.stream(array).skip(5).findFirst().orElse(null);
        return parameter;
    }

    public static Query getQuery(HttpServletRequest request) {
        String type = null;
        String search = null;
        IPageRequest<?> pageRequest = null;
        if (hasQueryString(request)) {
            Map<String, String> queryParams = extractQueryParameters(request);
            pageRequest = createPageRequest(queryParams);
            search = getParam(queryParams, "q");
            type = getParam(queryParams, "k");
        } else {
            pageRequest = buildPagination();
        }
        return new Query(pageRequest, search, type);
    }

    private static boolean hasQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString != null && !queryString.isEmpty();
    }

    private static Map<String, String> extractQueryParameters(HttpServletRequest request) {
        Map<String, String> queryParams = new HashMap<>();
        List<KeyPair> params = parseQueryParams(request.getQueryString());
        for (var param : params) {
            queryParams.put(param.getKey(), ((String) param.value()).trim());
        }
        return queryParams;
    }

    private static IPageRequest<?> createPageRequest(Map<String, String> queryParams) {
        int pageInitial = parsePageNumber(queryParams);
        int pageSize = parsePageSize(queryParams);
        Sort sort = createSort(queryParams);
        return PageRequest.builder()
                .initialPage(pageInitial)
                .pageSize(pageSize)
                .sort(sort)
                .build();
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
                queryParams.add(new KeyPair(pair[0], pair[DEFAULT_INITIAL_PAGE]));
            }
        }
        return queryParams;
    }

    private static PageRequest<Object> buildPagination() {
        int page = PropertiesUtil.getProperty("pagination.page", DEFAULT_INITIAL_PAGE);
        int size = PropertiesUtil.getProperty("pagination.limit", DEFAULT_MIN_PAGE_SIZE);
        String field = PropertiesUtil.getProperty("pagination.sort", DEFAULT_SORT_FIELD);
        String order = PropertiesUtil.getProperty("pagination.order", DEFAULT_SORT_ORDER);
        Sort sort = Sort.by(field).direction(Sort.Direction.from(order));
        return PageRequest.builder()
                .initialPage(page)
                .pageSize(size)
                .sort(sort)
                .build();
    }

    public static List<KeyPair> getParameters(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameterMap()
                .entrySet().stream()
                .map(e -> new KeyPair(e.getKey(), e.getValue()[0]))
                .toList();
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

    private static String getParam(Map<String, String> queryParams, String q) {
        String paramValue = queryParams.get(q);
        return paramValue != null ? URLDecoder.decode(paramValue, StandardCharsets.UTF_8) : null;
    }
}
