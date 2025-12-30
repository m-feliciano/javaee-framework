package com.servletstack.infrastructure.utils;

import com.servletstack.domain.vo.BinaryPayload;
import com.servletstack.infrastructure.config.Properties;
import com.servletstack.infrastructure.persistence.transfer.IPageRequest;
import com.servletstack.infrastructure.persistence.transfer.internal.PageRequest;
import com.servletstack.shared.vo.KeyPair;
import com.servletstack.shared.vo.Query;
import com.servletstack.shared.vo.Sort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

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
        String[] array = httpServletRequest.getRequestURI().split("/");
        return Arrays.stream(array).skip(5).findFirst().orElse(null);
    }

    public static IPageRequest getPageRequest(Query query) {
        return createPageRequest(query.parameters());
    }

    public static Map<String, String> filterQueryParameters(String queryString) {
        if (queryString == null || queryString.isEmpty()) return null;

        Map<String, String> queryParams = new HashMap<>();
        List<KeyPair> params = parseQueryParams(queryString);
        for (var param : params) {
            String parameterValue = ((String) param.value()).trim();
            String decoded = URLDecoder.decode(parameterValue, StandardCharsets.UTF_8);
            queryParams.put(param.getKey(), decoded);
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
            int maxPageSize = 100;
            return Math.min(Math.max(limit, DEFAULT_MIN_PAGE_SIZE), maxPageSize);
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

    public static PageRequest buildPageRequest() {
        int page = Properties.getOrDefault(PAGINATION_PAGE, DEFAULT_INITIAL_PAGE);
        int size = Properties.getOrDefault(PAGINATION_LIMIT, DEFAULT_MIN_PAGE_SIZE);
        String field = Properties.getOrDefault(PAGINATION_SORT, DEFAULT_SORT_FIELD);
        String order = Properties.getOrDefault(PAGINATION_ORDER, DEFAULT_SORT_ORDER);

        Sort sort = Sort.by(field).direction(Sort.Direction.from(order));
        return PageRequest.builder().initialPage(page).pageSize(size).sort(sort).build();
    }

    public static List<KeyPair> getParameters(HttpServletRequest req) {
        Map<String, String[]> parameters = req.getParameterMap();
        List<KeyPair> list = parameters.entrySet().stream()
                .filter(r -> !r.getKey().startsWith("X-"))
                .map(e -> new KeyPair(e.getKey(), e.getValue()[0]))
                .collect(Collectors.toList());

        if (req.getContentType() != null && req.getContentType().startsWith("multipart/form-data")) {
            list.add(new KeyPair("payload", buildMultPartPayload(req)));
        }

        return list;
    }

    public static String getErrorMessage(int status) {
        return switch (status) {
            case SC_BAD_REQUEST -> "Bad Request";
            case SC_UNAUTHORIZED -> "Unauthorized";
            case SC_SERVICE_UNAVAILABLE -> "Service Unavailable";
            case SC_FORBIDDEN -> "Forbidden";
            case SC_NOT_FOUND -> "Not Found";
            case SC_METHOD_NOT_ALLOWED -> "Method Not Allowed";
            case SC_CONFLICT -> "Conflict";
            case 429 -> "Too Many Requests";
            case SC_INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "Error";
        };
    }

    public static Query query(HttpServletRequest servletRequest) {
        Map<String, String> queries = filterQueryParameters(servletRequest.getQueryString());
        if (queries == null) return null;

        String field = queries.remove("k");
        String value = queries.remove("q");
        if (field != null && value != null) {
            queries.put(field, value);
        }

        return new Query(queries);
    }

    public static boolean matchWildcard(String endpoint, String eventName) {
        String regex = endpoint.replace("*", ".*");
        return eventName.matches(regex);
    }

    private static BinaryPayload buildMultPartPayload(HttpServletRequest req) {
        try {
            if (req.getParts() == null) return null;

            var part = req.getPart("file");

            if (part == null
                || part.getSize() == 0
                || part.getSubmittedFileName() == null
                || part.getInputStream() == null
                || part.getInputStream().available() == 0) return null;

            File tmp = File.createTempFile("upload_" + UUID.randomUUID().toString().substring(8), ".tmp");
            FileUtils.copyInputStreamToFile(part.getInputStream(), tmp);
            return new BinaryPayload(tmp.getAbsolutePath(), tmp.length(), part.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("Error processing multipart form data: " + e.getMessage(), e);
        }
    }
}
