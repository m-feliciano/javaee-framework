package com.dev.servlet.adapter.in.web.dto;

import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.shared.vo.Query;
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

    public boolean contains(String name) {
        return endpoint != null && endpoint.contains(name);
    }
}
