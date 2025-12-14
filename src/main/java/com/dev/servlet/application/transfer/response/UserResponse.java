package com.dev.servlet.application.transfer.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private String refreshToken;
    private List<Integer> roles;
    private String imgUrl;
    private Boolean unconfirmedEmail;
    private Boolean created;

    public UserResponse(String id) {
        this.id = id;
    }

    public boolean hasToken() {
        return token != null || refreshToken != null;
    }
}
