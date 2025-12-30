package com.servletstack.adapter.in.web.dispatcher;

import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.Request;

@FunctionalInterface
public interface HttpExecutor {
    IHttpResponse<?> send(Request request);
}
