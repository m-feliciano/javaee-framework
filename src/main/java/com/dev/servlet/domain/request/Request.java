package com.dev.servlet.domain.request;

import com.dev.servlet.core.util.CloneUtil;
import com.dev.servlet.domain.records.Query;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
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
