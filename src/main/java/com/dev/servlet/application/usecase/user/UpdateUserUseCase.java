package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.UpdateUserUseCasePort;
import com.dev.servlet.application.port.in.user.UserDetailsUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.domain.enums.MessageType;
import com.dev.servlet.infrastructure.persistence.repository.UserRepository;
import com.dev.servlet.infrastructure.alert.AlertService;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.messaging.Message;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class UpdateUserUseCase implements UpdateUserUseCasePort {
    private static final String CACHE_KEY = "userCacheKey";
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    @Named("messageProducer")
    private MessagePort messagePort;
    @Inject
    private AuthenticationPort authPort;
    @Inject
    private AlertService alertService;
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserDetailsUseCasePort userDetailsUseCasePort;
    @Inject
    private GenerateConfirmationTokenUseCase generateConfirmationTokenUseCase;
    private String baseUrl;

    @PostConstruct
    public void init() {
        baseUrl = Properties.getEnvOrDefault("APP_BASE_URL", "http://localhost:8080");
    }

    public UserResponse update(UserRequest userRequest, String auth) throws ApplicationException {
        log.debug("UpdateUserUseCase: updating user with id {}", userRequest.id());
        String userId = authPort.extractUserId(auth);

        final String email = userRequest.login().toLowerCase();
        boolean emailUnavailable = !isEmailAvailable(email, userMapper.toUser(userRequest));
        if (emailUnavailable) {
            log.debug("UpdateUserUseCase: email {} is already in use", email);

            auditPort.warning("user:update", auth, new AuditPayload<>(userRequest.id(), null));
            alertService.publish(userId, "warning", "The email address is already in use.");
            return userDetailsUseCasePort.get(userId, auth);
        }

        UserResponse entity = userDetailsUseCasePort.get(userId, auth);
        String oldEmail = entity.getLogin();
        User user = User.builder()
                .id(entity.getId())
                .imgUrl(userRequest.imgUrl())
                .credentials(Credentials.builder()
                        .login(entity.getLogin())
                        .password(userRequest.password())
                        .build())
                .status(Status.ACTIVE.getValue())
                .perfis(entity.getPerfis())
                .build();

        try {
            user = userRepository.update(user);
            log.debug("UpdateUserUseCase: user with id {} updated successfully", user.getId());
            CacheUtils.clear(entity.getId(), CACHE_KEY);

        } catch (Exception e) {
            auditPort.failure("user:update", auth, new AuditPayload<>(userRequest.id(), null));
            throw e;
        }

        if (!oldEmail.equals(email)) {
            String token = generateConfirmationTokenUseCase.execute(user, email);
            String link = this.baseUrl + "/api/v1/user/email-change-confirmation?token=" + token;
            String createdAt = OffsetDateTime.now().toString();
            messagePort.send(new Message(MessageType.CHANGE_EMAIL, email, createdAt, link));
            String info = "Email change requested for userId: " + user.getId();

            auditPort.info("user:email-change-confirmation", auth,
                    new AuditPayload<>(userRequest.id(), info));
            alertService.publish(user.getId(), "info", "A confirmation email has been sent to your new email address.");

        } else {
            alertService.publish(user.getId(), "success", "Your profile has been updated successfully.");
        }

        UserResponse response = userMapper.toResponse(user);
        auditPort.success("user:update", auth, new AuditPayload<>(userRequest.id(), response));
        return response;
    }

    private boolean isEmailAvailable(String email, User candidate) {
        User filter = new User(email, null, Status.ACTIVE);
        return userRepository.find(filter)
                .map(u -> u.getId().equals(candidate.getId()))
                .orElse(true);
    }
}
