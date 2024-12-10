package com.dev.servlet.pojo.records;

import com.dev.servlet.interfaces.IHttpResponse;
import lombok.Builder;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * This record is used to represent the HTTP response.
 *
 * @param <T> type of response
 */
@Builder(builderMethodName = "newBuilder")
public record HttpResponse<T>(int statusCode, T body, String next, Set<String> errors) implements IHttpResponse<T> {

    public static HttpResponse<Integer> of(int status) {
        return new HttpResponse<>(status, null, null, null);
    }

    /**
     * Create a response with the response.
     *
     * @param response {@linkplain HttpResponse}
     * @param <U>      type of response
     * @return {@linkplain HttpResponse}
     */
    public static <U> HttpResponse<U> of(U response) {
        return new HttpResponse<>(HttpServletResponse.SC_OK, response, null, null);
    }

    /**
     * Create a response with errors.
     *
     * @param status status code
     * @param errors errors
     * @param <U>    type of response
     * @return {@linkplain HttpResponse}
     */
    public static <U> HttpResponse<U> ofError(int status, Set<String> errors) {
        return new HttpResponse<>(status, null, null, errors);
    }

    /**
     * @see HttpResponse#ofError(int, Set)
     */
    public static <U> HttpResponse<U> ofError(int status, String error) {
        return ofError(status, Set.of(error));
    }

    /**
     * Create a response with next path.
     *
     * @param next next path
     * @param <U>  type of response
     * @return {@linkplain HttpResponse}
     */
    public static <U> HttpResponse<U> ofNext(String next) {
        return new HttpResponse<>(HttpServletResponse.SC_OK, null, next, null);
    }
}