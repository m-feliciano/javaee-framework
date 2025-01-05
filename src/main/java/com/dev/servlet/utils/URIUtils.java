package com.dev.servlet.utils;

import com.dev.servlet.pojo.enums.Order;
import com.dev.servlet.pojo.enums.Sort;
import com.dev.servlet.pojo.records.KeyPair;
import com.dev.servlet.pojo.records.Pagination;
import com.dev.servlet.pojo.records.Query;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to extract the URI information from the request.
 *
 * @since 1.4
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class URIUtils {

    public static final String URI_INTERNAL_CACHE_KEY = "uri_internal_cache_key";

    /**
     * This action is used to get the service key from the request.
     *
     * @param request {@linkplain HttpServletRequest}
     * @return service key
     */
    public static String getServicePath(HttpServletRequest request) {
        return getPathURI(request, 1);
    }

    /**
     * Get the action from the request.
     *
     * @param request {@linkplain HttpServletRequest}
     * @return action key
     */
    public static String getServiceName(HttpServletRequest request) {
        return getPathURI(request, 2);
    }

    /**
     * Extracts a part of the URI based on the given index.
     * <p>
     * Obs: the uri must be in the format /view/{service}/{action}/{resourceId|query} or /view/{service}/{action}
     * <p>
     *
     * @param request {@linkplain HttpServletRequest}
     * @param index   the index of the part to extract
     * @return the extracted part of the URI
     */
    private static String getPathURI(HttpServletRequest request, int index) {
        String pathInfo = request.getServletPath();
        String uri = Arrays.stream(pathInfo.split("/"))
                .filter(s -> !s.isBlank())
                .skip(index)
                .findFirst()
                .orElse(null);

        if (uri == null) return null;
        return "/" + uri;
    }


    /**
     * Return the resource id from the request.
     * The resource id can be passed as a parameter or as part of the URI.
     *
     * @param httpServletRequest {@linkplain HttpServletRequest}
     * @return
     */
    public static String getResourceId(HttpServletRequest httpServletRequest) {
        String parameter = httpServletRequest.getParameter("id");
        if (parameter == null) {
            String[] array = httpServletRequest.getRequestURI().split("/");
            parameter = Arrays.stream(array).skip(4).findFirst().orElse(null);
        }

        return parameter;
    }

    /**
     * Create the query object from the request.
     *
     * @param request {@linkplain HttpServletRequest}
     * @return {@linkplain Query}
     */
    public static Query getQuery(HttpServletRequest request) {
        String search = null;
        String type = null;

        Pagination pagination = null;
        if (request.getQueryString() != null) {
            int page = 1;
            int size = 5;
            Sort sort = Sort.ID;
            Order order = Order.ASC;

            List<KeyPair> params = parseQueryParams(request.getQueryString());
            for (var param : params) {
                final String value = ((String) param.value()).trim();

                switch (param.getKey()) {
                    case "page", "limit" -> {
                        int integer = Math.max(Math.abs(Integer.parseInt(value)), 1);

                        if (param.getKey().equals("page")) page = integer;
                        else size = integer;
                    }
                    case "sort" -> sort = Sort.from(value);
                    case "order" -> order = Order.from(value);
                    case "q" -> search = URLDecoder.decode(value, StandardCharsets.UTF_8);
                    case "k" -> type = value;
                    default -> { // Do nothing
                    }
                }

                pagination = Pagination.builder().currentPage(page).pageSize(size).sort(sort).order(order).build();
            }
        }

        pagination = Optional.ofNullable(pagination).orElseGet(URIUtils::getDefaultPageValue);
        return new Query(pagination, search, type);
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
                queryParams.add(new KeyPair(pair[0], pair[1]));
            }
        }

        return queryParams;
    }

    /**
     * Get the default query value.
     * If the value is not found in the cache, then it will be created and stored in the cache.
     * The default values are read from the properties file.
     *
     * @return {@linkplain Pagination}
     */
    private static Pagination getDefaultPageValue() {
        List<Serializable> data = CacheUtil.get(URI_INTERNAL_CACHE_KEY, "default_pagination_internal");
        if (!CollectionUtils.isEmpty(data)) {
            Object object = data.get(0);
            return (Pagination) object;
        }

        int page = PropertiesUtil.getProperty("pagination.page", 1);
        int size = PropertiesUtil.getProperty("pagination.limit", 5);
        Sort sort = Sort.from(PropertiesUtil.getProperty("pagination.sort", "id"));
        Order order = Order.from(PropertiesUtil.getProperty("pagination.order", "asc"));

        Pagination pagination = Pagination.builder().currentPage(page).pageSize(size).sort(sort).order(order).build();

        CacheUtil.set(URI_INTERNAL_CACHE_KEY, "default_pagination_internal", List.of(pagination));

        return pagination;
    }

    /**
     * Get the parameters from the request.
     *
     * @param httpServletRequest {@linkplain HttpServletRequest}
     * @return {@linkplain List} of {@linkplain KeyPair}
     */
    public static List<KeyPair> getParameters(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameterMap().entrySet().stream()
                .map(e -> new KeyPair(e.getKey(), e.getValue()[0]))
                .toList();
    }

    /**
     * Get the error message based on the status code.
     *
     * @param status
     * @return
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
     * @return
     * @author marcelo.feliciano
     */
    public static String getEndpoint(HttpServletRequest httpServletRequest) {
        String servicePath = getServicePath(httpServletRequest);
        String serviceName = getServiceName(httpServletRequest);
        if (serviceName == null) return servicePath;

        return servicePath + serviceName;
    }
}
