package com.dev.servlet.application.transfer.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private String id;
    private String name;
    private String status;

    public CategoryResponse(String id) {
        this.id = id;
    }
}
