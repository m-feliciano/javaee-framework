package com.dev.servlet.web;

import com.dev.servlet.domain.valueobject.Query;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
    private String endpoint;
    private String method;
    private String jsonBody;
    private String token;
    private IPageRequest pageRequest;
    private Query query;
    private int retry;

    public <T> Object getPayload(Class<T> clazz) {
        return CloneUtil.fromJson(jsonBody, clazz);
    }

    public boolean contains(String logout) {
        return endpoint != null && endpoint.contains(logout);
    }
}
