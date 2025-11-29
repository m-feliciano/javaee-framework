package com.dev.servlet.application.port.out.repository;

import com.dev.servlet.domain.entity.RefreshToken;

import java.util.Collection;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    Optional<RefreshToken> findByToken(String token);

    Collection<RefreshToken> findAll(RefreshToken object);

    void revokeAll(String userId);
}

