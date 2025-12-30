package com.servletstack.application.transfer.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryRequest(UUID id,
                              String name,
                              String status) {
}
