package com.dev.servlet.domain.request;

import com.dev.servlet.domain.records.Query;
import com.dev.servlet.infrastructure.persistence.IPageRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(jsonBody, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean contains(String logout) {
        return endpoint != null && endpoint.contains(logout);
    }
}
