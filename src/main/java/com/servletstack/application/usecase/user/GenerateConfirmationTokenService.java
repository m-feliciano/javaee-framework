package com.servletstack.application.usecase.user;

import com.servletstack.application.port.in.user.GenerateConfirmationTokenUseCase;
import com.servletstack.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.servletstack.domain.entity.ConfirmationToken;
import com.servletstack.domain.entity.User;
import com.servletstack.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GenerateConfirmationTokenService implements GenerateConfirmationTokenUseCase {
    @Inject
    private ConfirmationTokenRepositoryPort repository;

    public String generateFor(User user, Object body) {
        log.debug("Generating confirmation token for user {}", user.getId());

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .userId(user.getId())
                .createdAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusMinutes(30))
                .body(CloneUtil.toJson(body))
                .used(false)
                .build();

        repository.save(confirmationToken);
        log.debug("Generated confirmation token for user {}: {}", user.getId(), token);
        return token;
    }
}
