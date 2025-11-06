package com.dev.servlet.domain.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class UserResponse {
    private String id;
    private String login;
    private String password;
    private String imgUrl;
    private String token;
    private String refreshToken;
    private List<Integer> perfis;

    public UserResponse(String id) {
        this.id = id;
    }
}
