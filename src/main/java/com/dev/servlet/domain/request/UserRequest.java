package com.dev.servlet.domain.request;

import lombok.Builder;

@Builder
public record UserRequest(String id,
                          String login,
                          String password,
                          String imgUrl) {

    public UserRequest(String id) {
        this(id, null, null, null);
    }

    public UserRequest forAudit() {
        return UserRequest.builder().id(this.id).login(this.login).build();
    }
}
