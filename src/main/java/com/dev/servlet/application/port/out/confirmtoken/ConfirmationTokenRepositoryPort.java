package com.dev.servlet.application.port.out.confirmtoken;

import com.dev.servlet.domain.entity.ConfirmationToken;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenRepositoryPort {
    Optional<ConfirmationToken> findByToken(String token);

    ConfirmationToken save(ConfirmationToken ct);

    ConfirmationToken update(ConfirmationToken ct);

    boolean existsValidTokenForUser(UUID userId);
}

