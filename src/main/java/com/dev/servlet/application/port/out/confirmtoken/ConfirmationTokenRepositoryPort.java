package com.dev.servlet.application.port.out.confirmtoken;

import com.dev.servlet.domain.entity.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenRepositoryPort {
    Optional<ConfirmationToken> findByToken(String token);

    ConfirmationToken save(ConfirmationToken confirmationToken);
}

