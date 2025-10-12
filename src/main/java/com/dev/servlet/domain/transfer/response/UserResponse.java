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
    private List<Long> perfis;
}
