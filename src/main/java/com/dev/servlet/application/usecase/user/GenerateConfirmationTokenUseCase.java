package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenPort;
import com.dev.servlet.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.shared.util.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class GenerateConfirmationTokenUseCase implements GenerateConfirmationTokenPort {
    @Inject
    private ConfirmationTokenRepositoryPort repositoryPort;

    public String generateFor(User user, Object body) {
        log.debug("Generating confirmation token for user {}", user.getId());

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .userId(user.getId())
                .createdAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusHours(1))
                .body(CloneUtil.toJson(body))
                .used(false)
                .build();

        repositoryPort.save(confirmationToken);
        log.debug("Generated confirmation token for user {}: {}", user.getId(), token);
        return token;
    }
}
