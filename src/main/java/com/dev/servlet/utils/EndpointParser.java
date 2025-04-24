package com.dev.servlet.utils;

import com.dev.servlet.pojo.records.Request;

import java.util.Arrays;

public class EndpointParser {

    private static final int API_VERSION_INDEX = 2;
    private static final int SERVICE_NAME_START_INDEX = 4;

    private final Request request;
    private String[] parts;

    public EndpointParser(Request request) {
        if (request == null || request.endpoint() == null || request.endpoint().isEmpty()) {
            throw new IllegalArgumentException("Request or endpoint cannot be null or empty");
        }

        this.request = request;
        parseEndpoint();
    }

    private void parseEndpoint() {
        if (parts == null) {
            String[] split = request.endpoint().split("/");
            if (split.length < SERVICE_NAME_START_INDEX) {
                throw new IllegalArgumentException("Invalid endpoint format: " + request.endpoint());
            }

            parts = new String[2];
            parts[0] = String.join("/", Arrays.copyOfRange(split, 0, SERVICE_NAME_START_INDEX));
            parts[1] = String.join("/", Arrays.copyOfRange(split, SERVICE_NAME_START_INDEX, split.length));
        }
    }

    public String getServiceName() {
        return parts[1];
    }

    public String getService() {
        String[] array = parts[0].split("/");
        return "/" + array[array.length - 1];
    }

    public String getApiVersion() {
        String[] array = parts[0].split("/");
        if (array.length <= API_VERSION_INDEX) {
            throw new IllegalArgumentException("API version not found in endpoint: " + request.endpoint());
        }
        return array[API_VERSION_INDEX];
    }
}