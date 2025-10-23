package com.dev.servlet.domain.transfer.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public final class UserResponse {
    private String id;
    private String login;
    private String password;
    private String imgUrl;
    private String token;
    private String refreshToken;
    private List<Long> perfis;

    public Object withoutToken() {
        return UserResponse.builder()
                .id(id).login(login).password(password).imgUrl(imgUrl).perfis(perfis)
                .build();
    }
}
