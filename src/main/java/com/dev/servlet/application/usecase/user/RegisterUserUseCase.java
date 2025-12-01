package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenUseCasePort;
import com.dev.servlet.application.port.in.user.RegisterUserUseCasePort;
import com.dev.servlet.application.port.in.user.UserDemoModeUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RoleType;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.cache.CacheUtils;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.persistence.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.dev.servlet.shared.enums.ConstantUtils.DEMO_USER_LOGIN;
import static com.dev.servlet.shared.util.ThrowableUtils.serviceError;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class RegisterUserUseCase implements RegisterUserUseCasePort {
    @Inject
    private UserRepository userRepository;
    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    @Named("messageProducer")
    private MessagePort messagePort;
    @Inject
    private GenerateConfirmationTokenUseCasePort generateConfirmationTokenUseCasePort;
    // Only for demo purposes
    @Inject
    private Instance<UserDemoModeUseCasePort> demoModeUseCasePortInstance;

    private String baseUrl;

    @PostConstruct
    public void init() {
        baseUrl = Properties.getEnvOrDefault("APP_BASE_URL", "http://localhost:8080");
    }

    public UserResponse register(UserCreateRequest userReq) throws ApplicationException {
        log.debug("RegisterUserUseCase: registering user with login {}", userReq.login());

        if (Properties.isDemoModeEnabled()) {
            log.debug("RegisterUserUseCase: demo mode is enabled, only {} user can be registered", DEMO_USER_LOGIN);
            UserDemoModeUseCasePort instance = demoModeUseCasePortInstance.get();
            User user = instance.validateCredentials(new LoginRequest(userReq.login(), userReq.password()));
            return userMapper.toResponse(user);
        }

        boolean passwordError = userReq.password() == null || !userReq.password().equals(userReq.confirmPassword());
        if (passwordError) {
            auditPort.failure("user:register", null, new AuditPayload<>(userReq, null));
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Passwords do not match.");
        }

        User user = User.builder()
                .credentials(Credentials.builder()
                        .login(userReq.login().toLowerCase())
                        .build())
                .build();
        User userExists = userRepository.find(user).orElse(null);
        if (userExists != null) {
            log.warn("User already exists: {}", userExists.getCredentials().getLogin());
            auditPort.failure("user:register", null, new AuditPayload<>(userReq, null));
            throw serviceError(HttpServletResponse.SC_FORBIDDEN, "Cannot register this user.");
        }

        User newUser = User.builder()
                .credentials(Credentials.builder()
                        .login(userReq.login().toLowerCase())
                        .password(userReq.password())
                        .build())
                .status(Status.PENDING.getValue())
                .perfis(List.of(RoleType.DEFAULT.getCode()))
                .build();

        newUser = userRepository.save(newUser);
        log.info("User registered: {}", newUser.getCredentials().getLogin());

        String token = generateConfirmationTokenUseCasePort.createTokenForUser(newUser, null);
        String url = this.baseUrl + "/api/v1/user/confirm?token=" + token;
        messagePort.sendConfirmation(newUser.getCredentials().getLogin(), url);

        UserResponse response = userMapper.toResponse(newUser);
        response.setCreated(true);
        CacheUtils.setObject(newUser.getId(), "userCacheKey", response);
        auditPort.success("user:register", null, new AuditPayload<>(userReq, response));
        return response;
    }
}
