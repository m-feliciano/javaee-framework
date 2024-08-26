package com.dev.servlet.builders;

import com.dev.servlet.filter.StandardPagination;
import com.dev.servlet.filter.StandardRequest;
import com.dev.servlet.utils.URIUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class RequestBuilder {

    private final Map<String, Object> parameters;

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public RequestBuilder() {
        parameters = new java.util.HashMap<>();
    }

    public RequestBuilder token(String token) {
        this.parameters.put("token", token);
        return this;
    }

    public RequestBuilder clazz(Class<?> clazz) {
        this.parameters.put("clazz", clazz);
        return this;
    }

    public RequestBuilder request(HttpServletRequest request) {
        this.parameters.put("request", request);
        return this;
    }

    public RequestBuilder response(HttpServletResponse response) {
        this.parameters.put("response", response);
        return this;
    }

    public StandardRequest build() {
        String action = URIUtils.getAction((HttpServletRequest) this.parameters.get("request"));
        this.parameters.put("action", action);

        return new StandardRequest(
                (HttpServletRequest) this.parameters.get("request"),
                (HttpServletResponse) this.parameters.get("response"),
                (String) this.parameters.get("action"),
                (Class<?>) this.parameters.get("clazz"),
                (String) this.parameters.get("token"),
                (StandardPagination) this.parameters.get("pagination"));
    }

    /**
     * This method creates a standard pagination object.
     */
    public RequestBuilder pagination() {
        int currentPage = 1;
        int pageSize = 5;

        if (this.parameters.get("request") != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) this.parameters.get("request");

            if (httpRequest.getParameter("pageSize") != null) {
                pageSize = Integer.parseInt(httpRequest.getParameter("pageSize"));
            }
            if (httpRequest.getParameter("page") != null) {
                currentPage = Integer.parseInt(httpRequest.getParameter("page"));
            }

            this.parameters.put("pagination", StandardPagination.of(currentPage, pageSize));
            httpRequest.setAttribute("pagination", parameters.get("pagination"));
        }

        return this;
    }

}
