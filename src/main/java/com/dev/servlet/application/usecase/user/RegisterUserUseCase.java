package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.GenerateConfirmationTokenPort;
import com.dev.servlet.application.port.in.user.RegisterUserPort;
import com.dev.servlet.application.port.in.user.UserDemoModePort;
import com.dev.servlet.application.port.out.MessagePort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.UserCreateRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RoleType;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.utils.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.dev.servlet.shared.enums.ConstantUtils.DEMO_USER_LOGIN;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Slf4j
@ApplicationScoped
public class RegisterUserUseCase implements RegisterUserPort {
    @Inject
    private UserRepositoryPort repositoryPort;
    @Inject
    @Named("sqsMessageProducer")
    private MessagePort messagePort;
    @Inject
    private GenerateConfirmationTokenPort generateConfirmationTokenPort;
    // Only for demo purposes
    @Inject
    private Instance<UserDemoModePort> demoModePortInstance;

    public UserResponse register(UserCreateRequest userReq) throws AppException {
        log.debug("RegisterUserUseCase: registering user with login {}", userReq.login());

        if (Properties.isDemoModeEnabled()) {
            log.debug("RegisterUserUseCase: demo mode is enabled, only {} user can be registered", DEMO_USER_LOGIN);
            UserDemoModePort instance = demoModePortInstance.get();
            User user = instance.validateCredentials(new LoginRequest(userReq.login(), userReq.password()));
            return new UserResponse(user.getId());
        }

        boolean passwordError = userReq.password() == null || !userReq.password().equals(userReq.confirmPassword());
        if (passwordError) throw new AppException(SC_FORBIDDEN, "Passwords do not match.");

        User user = User.builder()
                .credentials(Credentials.builder()
                        .login(userReq.login().toLowerCase())
                        .build())
                .build();
        User userExists = repositoryPort.find(user).orElse(null);
        if (userExists != null) {
            log.warn("User already exists: {}", userExists.getCredentials().getLogin());
            throw new AppException(SC_FORBIDDEN, "Cannot register this user.");
        }

        User newUser = User.builder()
                .credentials(Credentials.builder()
                        .login(userReq.login().toLowerCase())
                        .password(PasswordHasher.hash(userReq.password()))
                        .build())
                .status(Status.PENDING.getValue())
                .perfis(List.of(RoleType.DEFAULT.getCode()))
                .build();

        newUser = repositoryPort.save(newUser);
        log.info("User registered: {}", newUser.getCredentials().getLogin());

        String token = generateConfirmationTokenPort.generateFor(newUser, null);
        String link = Properties.getAppBaseUrl() + "/api/v1/user/confirm?token=" + token;
        messagePort.sendConfirmation(newUser.getCredentials().getLogin(), link);

        UserResponse response = new UserResponse(newUser.getId());
        response.setCreated(true);
        return response;
    }
}
