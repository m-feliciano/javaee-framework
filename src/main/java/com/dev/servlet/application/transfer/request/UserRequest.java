package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.vo.BinaryPayload;
import lombok.Builder;

@Builder
public record UserRequest(String id,
                          String login,
                          String password,
                          BinaryPayload payload) {

    public UserRequest(String id) {
        this(id, null, null, null);
    }

    public UserRequest(String login, String password) {
        this(null, login, password, null);
    }
}
