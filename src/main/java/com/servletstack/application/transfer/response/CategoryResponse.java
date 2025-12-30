package com.servletstack.application.transfer.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private String status;
}
