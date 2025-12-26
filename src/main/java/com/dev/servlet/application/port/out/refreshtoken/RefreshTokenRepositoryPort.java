package com.dev.servlet.application.port.out.refreshtoken;

import com.dev.servlet.domain.entity.RefreshToken;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    Optional<RefreshToken> findByToken(String token);

    Collection<RefreshToken> findAll(RefreshToken object);

    void revokeAll(UUID userId);

    RefreshToken save(RefreshToken rt);

    RefreshToken update(RefreshToken old);
}

