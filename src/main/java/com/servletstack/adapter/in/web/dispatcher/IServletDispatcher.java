package com.servletstack.adapter.in.web.dispatcher;

import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.adapter.in.web.dto.Request;

public interface IServletDispatcher {
    IHttpResponse<?> dispatch(Request request);
}
