package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.vo.BinaryPayload;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRequest(UUID id,
                          String login,
                          String password,
                          BinaryPayload payload) {

    public UserRequest(UUID id) {
        this(id, null, null, null);
    }

    public UserRequest(String login, String password) {
        this(null, login, password, null);
    }
}
