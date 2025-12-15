package com.dev.servlet.application.usecase.user;

import com.dev.servlet.adapter.out.messaging.Message;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenPort;
import com.dev.servlet.application.port.in.user.UpdateUserPort;
import com.dev.servlet.application.port.in.user.UserDetailsPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.port.out.alert.AlertPort;
import com.dev.servlet.application.port.out.cache.CachePort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.utils.PasswordHasher;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@ApplicationScoped
public class UpdateUserUseCase implements UpdateUserPort {
    @Inject
    private UserMapper userMapper;
    @Inject
    @Named("messageProducer")
    private MessagePort messagePort;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private AlertPort alertPort;
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    private UserDetailsPort userDetailsPort;
    @Inject
    private GenerateConfirmationTokenPort generateConfirmationTokenPort;
    private String baseUrl;
    @Inject
    private CachePort cachePort;

    @PostConstruct
    public void init() {
        baseUrl = Properties.getEnvOrDefault("APP_BASE_URL", "http://localhost:8080");
    }

    public UserResponse update(UserRequest userRequest, String auth) throws AppException {
        String userId = authPort.extractUserId(auth);

        if (Properties.isDemoModeEnabled()) {
            log.warn("UpdateUserUseCase: update users is not allowed in demo mode");
            alertPort.publish(userId, "warning", "Update user is not allowed in demo mode.");
            return userDetailsPort.getDetail(userId, auth);
        }

        final String newEmail = userRequest.login().toLowerCase();
        boolean emailUnavailable = !isEmailAvailable(newEmail, userMapper.toUser(userRequest));
        if (emailUnavailable) {
            alertPort.publish(userId, "warning", "The email address is already in use.");
            return userDetailsPort.getDetail(userId, auth);
        }

        User user = repositoryPort.findById(userId).orElseThrow();
        final String oldEmail = user.getLogin();

        repositoryPort.updateCredentials(userId,
                new Credentials(newEmail, PasswordHasher.hash(userRequest.password())));

        cachePort.clear("userCacheKey", user.getId());

        if (!oldEmail.equals(newEmail)) {
            String token = generateConfirmationTokenPort.generateFor(user, newEmail);
            String link = this.baseUrl + "/api/v1/user/email-change-confirmation?token=" + token;
            String createdAt = OffsetDateTime.now().toString();
            messagePort.send(new Message(MessageType.CHANGE_EMAIL, newEmail, createdAt, link));
            alertPort.publish(user.getId(), "info", "A confirmation email has been sent to your new email address.");
        } else {
            alertPort.publish(user.getId(), "success", "Your profile has been updated successfully.");
        }

        return new UserResponse(user.getId());
    }

    private boolean isEmailAvailable(String email, User candidate) {
        User filter = new User(email, null, Status.ACTIVE);
        return repositoryPort.find(filter)
                .map(u -> u.getId().equals(candidate.getId()))
                .orElse(true);
    }
}
