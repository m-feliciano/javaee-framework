package com.dev.servlet.utils;

import com.dev.servlet.pojo.Pageable;
import com.dev.servlet.pojo.records.KeyPair;
import com.dev.servlet.pojo.records.Query;
import com.dev.servlet.pojo.records.Sort;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to extract the URI information from the request.
 *
 * @since 1.4
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class URIUtils {

    public static final String URI_INTERNAL_CACHE_KEY = "uri_internal_cache_key";
    public static final String DEFAULT_SORT_FIELD = "id";
    public static final String DEFAULT_SORT_ORDER = "asc";
    public static final int DEFAULT_PAGE_LIMIT = 5;
    public static final int DEFAULT_PAGE_INITIAL = 1;

    /**
     * This action is used to get the service key from the request.
     *
     * @param request {@linkplain HttpServletRequest}
     * @return service key
     */
    public static String getServicePath(HttpServletRequest request) {
        return getPathURI(request, 2);
    }

    /**
     * Get the action from the request.
     *
     * @param request {@linkplain HttpServletRequest}
     * @return action key
     */
    public static String getServiceName(HttpServletRequest request) {
        return getPathURI(request, 3);
    }

    /**
     * Extracts a part of the URI based on the given index.
     * <p>
     * Obs: the uri must be in the format /{service}/{action}/{resourceId|query} or /{service}/{action}
     * <p>
     *
     * @param request {@linkplain HttpServletRequest}
     * @param index   the index of the part to extract
     * @return the extracted part of the URI
     */
    private static String getPathURI(HttpServletRequest request, int index) {
        String pathInfo = request.getServletPath();

        return Arrays.stream(pathInfo.split("/"))
                .filter(s -> !s.isBlank())
                .skip(index)
                .findFirst()
                .orElse(null);
    }


    /**
     * Return the resource id from the request.
     * The resource id can be passed as a parameter or as part of the URI.
     *
     * @param httpServletRequest {@linkplain HttpServletRequest}
     */
    public static String getResourceId(HttpServletRequest httpServletRequest) {
        String parameter = httpServletRequest.getParameter("id");
        if (parameter != null) return parameter;

        String[] array = httpServletRequest.getRequestURI().split("/");
        parameter = Arrays.stream(array).skip(5).findFirst().orElse(null);
        return parameter;
    }

    /**
     * Create the query object from the request.
     *
     * @param request {@linkplain HttpServletRequest}
     * @return {@linkplain Query}
     */
    public static Query getQuery(HttpServletRequest request) {
        HashMap<String, String> queryParams = new HashMap<>();
        Pageable pageable;

        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            List<KeyPair> params = parseQueryParams(request.getQueryString());
            for (var param : params) {
                queryParams.put(param.getKey(), ((String) param.value()).trim());
            }

            int page = Math.abs(Integer.parseInt(queryParams.getOrDefault("page", String.valueOf(DEFAULT_PAGE_INITIAL))));
            int pageInitial = Math.max(page, DEFAULT_PAGE_INITIAL);

            int limit = Math.abs(Integer.parseInt(queryParams.getOrDefault("limit", String.valueOf(DEFAULT_PAGE_LIMIT))));
            int pageSize = Math.max(limit, DEFAULT_PAGE_LIMIT);

            String sortField = queryParams.getOrDefault("sort", DEFAULT_SORT_FIELD);
            Sort.Direction direction = Sort.Direction.from(queryParams.getOrDefault("order", DEFAULT_SORT_ORDER));

            pageable = Pageable.builder()
                    .currentPage(pageInitial)
                    .pageSize(pageSize)
                    .sort(Sort.of(sortField, direction))
                    .build();

            String search = getParam(queryParams, "q");
            String type = getParam(queryParams, "k");

            return new Query(pageable, search, type);
        }

        pageable = getDefaultPageValue();
        return new Query(pageable, null, null);
    }

    /**
     * Parse the query parameters from the request.
     *
     * @param query
     * @return {@linkplain List} of {@linkplain KeyPair}
     */
    private static List<KeyPair> parseQueryParams(String query) {
        List<KeyPair> queryParams = new ArrayList<>();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                queryParams.add(new KeyPair(pair[0], pair[DEFAULT_PAGE_INITIAL]));
            }
        }

        return queryParams;
    }

    /**
     * Get the default query value.
     * If the value is not found in the cache, then it will be created and stored in the cache.
     * The default values are read from the properties file.
     *
     * @return {@linkplain Pageable}
     */
    public static Pageable getDefaultPageValue() {
        List<Serializable> data = CacheUtil.get(URI_INTERNAL_CACHE_KEY, "default_pagination_internal");
        if (!CollectionUtils.isEmpty(data)) {
            Object object = data.get(0);
            return (Pageable) object;
        }

        var pagination = buildPagination();

        CacheUtil.set(URI_INTERNAL_CACHE_KEY, "default_pagination_internal", List.of(pagination));

        return pagination;
    }

    /**
     * Build the pagination object.
     */
    private static Pageable buildPagination() {
        int page = PropertiesUtil.getProperty("pagination.page", DEFAULT_PAGE_INITIAL);
        int size = PropertiesUtil.getProperty("pagination.limit", DEFAULT_PAGE_LIMIT);
        String sortField = PropertiesUtil.getProperty("pagination.sort", DEFAULT_SORT_FIELD);
        String order = PropertiesUtil.getProperty("pagination.order", DEFAULT_SORT_ORDER);

        Sort sort = Sort.of(sortField, Sort.Direction.from(order));

        return Pageable.builder().currentPage(page).pageSize(size).sort(sort).build();
    }

    /**
     * Get the parameters from the request.
     *
     * @param httpServletRequest {@linkplain HttpServletRequest}
     * @return {@linkplain List} of {@linkplain KeyPair}
     */
    public static List<KeyPair> getParameters(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameterMap()
                .entrySet().stream()
                .map(e -> new KeyPair(e.getKey(), e.getValue()[0]))
                .toList();
    }

    /**
     * Get the error message based on the status code.
     *
     * @param status
     * @see HttpServletResponse for the status codes
     */
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

    /**
     * Get the endpoint from the request.
     *
     * @param httpServletRequest {@linkplain HttpServletRequest}
     * @author marcelo.feliciano
     */
    public static String getEndpoint(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getServletPath();
    }

    private static String getParam(HashMap<String, String> queryParams, String q) {
        String paramValue = queryParams.get(q);
        return paramValue != null ? URLDecoder.decode(paramValue, StandardCharsets.UTF_8) : null;
    }
}
