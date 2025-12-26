package com.dev.servlet.adapter.in.web.dto;

import com.dev.servlet.domain.entity.enums.RequestMethod;
import com.dev.servlet.shared.vo.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = {"token", "payload"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
    private String endpoint;
    private RequestMethod method;
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
