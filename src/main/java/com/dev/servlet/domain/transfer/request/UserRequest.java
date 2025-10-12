package com.dev.servlet.domain.transfer.request;

import lombok.Builder;

@Builder
public record UserRequest(String id,
                          String login,
                          String password,
                          String imgUrl) {
}
