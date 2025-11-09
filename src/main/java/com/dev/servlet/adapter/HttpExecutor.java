package com.dev.servlet.adapter;

import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.Request;

@FunctionalInterface
public interface HttpExecutor<J> {
    IHttpResponse<J> send(Request request);
}
