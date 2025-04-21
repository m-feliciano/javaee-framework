package com.dev.servlet.utils;

import com.dev.servlet.pojo.records.Request;

import java.util.Arrays;

public class EndpointParser {

    private final Request request;
    private String[] parts;

    public EndpointParser(Request request) {
        this.request = request;
        initData();
    }

    private void initData() {
        String[] split = request.endpoint().split("/");

        parts = new String[2];
        parts[0] = String.join("/", Arrays.copyOfRange(split, 0, 3));
        parts[1] = String.join("/", Arrays.copyOfRange(split, 3, split.length));
    }

    public String getServiceName() {
        String serviceName = parts[1];
        return serviceName;
    }

    public String getService() {
        String service = parts[0];
        String[] array = service.split("/");
        return array[array.length - 1];
    }

    public String getApiVersion() {
        String service = parts[0];
        String[] array = service.split("/");
        return array[1];
    }
}