package com.dev.servlet.builders;

import com.dev.servlet.pojo.records.Order;
import com.dev.servlet.pojo.records.Pagable;
import com.dev.servlet.pojo.records.RequestObject;
import com.dev.servlet.pojo.records.Sort;
import com.dev.servlet.pojo.records.StandardRequest;
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

    public RequestBuilder service(String service) {
        this.parameters.put("service", service);
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
        if (!this.parameters.containsKey("request") || !this.parameters.containsKey("response")) {
            throw new IllegalArgumentException("Request and response are required");
        }

        var httpServletRequest = (HttpServletRequest) this.parameters.get("request");
        var httpServletResponse = (HttpServletResponse) this.parameters.get("response");

        this.parameters.putIfAbsent("service", URIUtils.service(httpServletRequest));
        this.parameters.putIfAbsent("action", URIUtils.action(httpServletRequest));
        this.parameters.putIfAbsent("resourceId", URIUtils.recourceId(httpServletRequest));

        RequestObject requestObject = new RequestObject(
                (String) this.parameters.get("action"),
                (String) this.parameters.get("service"),
                (Long) this.parameters.get("resourceId"),
                (String) this.parameters.get("token"),
                (Pagable) this.parameters.get("pagination"));

        return new StandardRequest(httpServletRequest, httpServletResponse, requestObject);
    }

    /**
     * This method creates a standard pagination object.
     */
    public RequestBuilder pagination() {
        int currentPage = 1;
        int pageSize = 5;
        Sort sort = Sort.ID;
        Order order = Order.DESC;

        HttpServletRequest request = (HttpServletRequest) this.parameters.get("request");
        String query = request.getQueryString();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length != 2) continue;
                if (pair[0].equals("page")) currentPage = Integer.parseInt(pair[1]);
                if (pair[0].equals("page_size")) pageSize = Math.min(Integer.parseInt(pair[1]), 100);
                if (pair[0].equals("sort")) sort = Sort.from(pair[1]);
                if (pair[0].equals("order")) order = Order.from(pair[1]);
            }
        }

        Pagable pagination = new Pagable();
        pagination.setCurrentPage(currentPage);
        pagination.setPageSize(pageSize);
        pagination.setSort(sort);
        pagination.setOrder(order);

        request.setAttribute("pagination", pagination);
        this.parameters.put("pagination", pagination);

        return this;
    }

}
