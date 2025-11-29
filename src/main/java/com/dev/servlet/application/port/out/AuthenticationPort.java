package com.dev.servlet.application.port.out;

import com.dev.servlet.domain.entity.User;

import java.util.List;

public interface AuthenticationPort {

    boolean validateToken(String token);

    String extractUserId(String token);

    List<Integer> extractRoles(String token);

    com.dev.servlet.domain.entity.User extractUser(String token);

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String stripBearerPrefix(String token);
}
