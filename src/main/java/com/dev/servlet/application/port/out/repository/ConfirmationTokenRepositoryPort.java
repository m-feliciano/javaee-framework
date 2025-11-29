package com.dev.servlet.application.port.out.repository;

import com.dev.servlet.domain.entity.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenRepositoryPort {
    Optional<ConfirmationToken> findByToken(String token);
}

