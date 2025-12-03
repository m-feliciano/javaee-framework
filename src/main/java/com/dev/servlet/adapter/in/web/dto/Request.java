package com.dev.servlet.adapter.in.web.dto;

import com.dev.servlet.shared.vo.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
    private String endpoint;
    private String method;
    private Query query;
    private Integer retry;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private String payload;

    public boolean contains(String name) {
        return endpoint != null && endpoint.contains(name);
    }
}
