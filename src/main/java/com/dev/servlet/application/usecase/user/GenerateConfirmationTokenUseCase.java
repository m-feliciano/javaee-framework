package com.dev.servlet.application.usecase.user;

import com.dev.servlet.domain.entity.ConfirmationToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.infrastructure.persistence.repository.ConfirmationTokenRepository;
import com.dev.servlet.infrastructure.utils.CloneUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class GenerateConfirmationTokenUseCase {
    @Inject
    private ConfirmationTokenRepository confirmationTokenDAO;

    public String execute(User user, Object body) {
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

        confirmationTokenDAO.save(confirmationToken);
        log.debug("Generated confirmation token for user {}: {}", user.getId(), token);
        return token;
    }
}
