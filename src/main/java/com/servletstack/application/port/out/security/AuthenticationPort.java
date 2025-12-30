package com.servletstack.application.port.out.security;

import com.servletstack.domain.entity.User;

import java.util.List;
import java.util.UUID;

public interface AuthenticationPort {

    boolean validateToken(String token);

    UUID extractUserId(String token);

    List<Integer> extractRoles(String token);

    User extractUser(String token);

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String stripBearerPrefix(String token);
}
