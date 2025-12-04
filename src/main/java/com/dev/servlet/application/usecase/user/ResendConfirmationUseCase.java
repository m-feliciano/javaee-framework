package com.dev.servlet.application.usecase.user;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenPort;
import com.dev.servlet.application.port.in.user.ResendConfirmationPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.ResendConfirmationRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ResendConfirmationUseCase implements ResendConfirmationPort {
    private static final String CACHE_KEY = "userCacheKey";
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private UserMapper userMapper;
    @Inject
    @Named("messageProducer")
    private MessagePort messagePort;
    @Inject
    private GenerateConfirmationTokenPort generateConfirmationTokenPort;
    private String baseUrl;
    @Inject
    private CachePort cachePort;

    @PostConstruct
    public void init() {
        baseUrl = Properties.getEnvOrDefault("APP_BASE_URL", "http://localhost:8080");
    }

    @Override
    public void resend(ResendConfirmationRequest request) throws ApplicationException {
        final String userId = request.userId();
        log.debug("ResendConfirmationUseCase: resending confirmation for userId {}", userId);

        if (StringUtils.isBlank(userId)) {
            log.warn("ResendConfirmationUseCase: userId is blank");
            return;
        }

        Optional<User> maybe = repositoryPort.findById(userId);
        if (maybe.isEmpty()) {
            log.warn("ResendConfirmationUseCase: unknown userId {}", userId);
            return;
        }

        User user = maybe.get();
        if (!Status.PENDING.getValue().equals(user.getStatus())) {
            log.info("ResendConfirmationUseCase: user {} not pending (status={}), skip", userId, user.getStatus());
            return;
        }

        String token = generateConfirmationTokenPort.generateFor(user, null);
        String link = baseUrl + "/api/v1/user/confirm?token=" + token;
        String email = user.getCredentials().getLogin();
        String createdAt = OffsetDateTime.now().toString();

        Message confirmation = new Message(userId, MessageType.CONFIRMATION, email, createdAt, link);
        messagePort.send(confirmation);

        UserResponse response = userMapper.toResponse(user);
        cachePort.setObject(user.getId(), CACHE_KEY, response);
    }
}
