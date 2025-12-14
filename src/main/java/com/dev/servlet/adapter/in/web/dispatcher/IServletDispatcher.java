package com.dev.servlet.adapter.in.web.dispatcher;

import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.adapter.in.web.dto.Request;

public interface IServletDispatcher {
    IHttpResponse<?> dispatch(Request request);
}
