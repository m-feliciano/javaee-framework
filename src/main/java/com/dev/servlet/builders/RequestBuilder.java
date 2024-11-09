package com.dev.servlet.builders;

import com.dev.servlet.pojo.records.StandardRequest;
import com.dev.servlet.pojo.records.Query;
import com.dev.servlet.pojo.records.RequestObject;
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
        var httpServletRequest = (HttpServletRequest) this.parameters.get("request");
        var httpServletResponse = (HttpServletResponse) this.parameters.get("response");

        if (httpServletRequest == null || httpServletResponse == null) {
            throw new IllegalArgumentException("Request and response are required");
        }

        String service = URIUtils.service(httpServletRequest);
        this.parameters.putIfAbsent("service", service);

        String action = URIUtils.action(httpServletRequest);
        this.parameters.putIfAbsent("action", action);

        Long id = URIUtils.recourceId(httpServletRequest);
        this.parameters.putIfAbsent("id", id);

        RequestObject requestObject = new RequestObject(service, action, id,
                (Query) httpServletRequest.getAttribute("query"),
                (String) this.parameters.get("token"));

        return new StandardRequest(httpServletRequest, httpServletResponse, requestObject);
    }

    /**
     * This action is used to get the query from the request.
     *
     * @return
     */
    public RequestBuilder query() {
        var httpServletRequest = (HttpServletRequest) this.parameters.get("request");
        httpServletRequest.setAttribute("query", URIUtils.query(httpServletRequest));
        return this;
    }

}
