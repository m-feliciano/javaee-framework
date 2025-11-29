package com.dev.servlet.application.transfer.request;

import lombok.Builder;

@Builder
public record CategoryRequest(String id,
                              String name,
                              String status) {
}
