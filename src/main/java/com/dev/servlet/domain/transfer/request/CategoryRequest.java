package com.dev.servlet.domain.transfer.request;

import lombok.Builder;

@Builder
public record CategoryRequest(String id,
                              String name,
                              String status) {
}
