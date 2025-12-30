package com.servletstack.application.usecase.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.in.user.GenerateConfirmationTokenUseCase;
import com.servletstack.application.port.in.user.RegisterUserUseCase;
import com.servletstack.application.port.in.user.UserDemoModeUseCase;
import com.servletstack.application.port.out.AsyncMessagePort;
import com.servletstack.application.port.out.user.UserRepositoryPort;
import com.servletstack.application.transfer.request.LoginRequest;
import com.servletstack.application.transfer.request.UserCreateRequest;
import com.servletstack.application.transfer.response.UserResponse;
import com.servletstack.domain.entity.Credentials;
import com.servletstack.domain.entity.User;
import com.servletstack.domain.entity.enums.RoleType;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.infrastructure.config.Properties;
import com.servletstack.infrastructure.utils.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.servletstack.shared.enums.ConstantUtils.DEMO_USER_LOGIN;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Slf4j
@ApplicationScoped
public class RegisterUserService implements RegisterUserUseCase {
    @Inject
    private UserRepositoryPort repository;
    @Inject
    private AsyncMessagePort messagePort;
    @Inject
    private GenerateConfirmationTokenUseCase generateConfirmationTokenUseCase;
    // Only for demo purposes
    @Inject
    private Instance<UserDemoModeUseCase> demoModePortInstance;

    public UserResponse register(UserCreateRequest userReq) throws AppException {
        log.debug("RegisterUserUseCase: registering user with login {}", userReq.login());

        if (Properties.isDemoModeEnabled()) {
            log.debug("RegisterUserUseCase: demo mode is enabled, only {} user can be registered", DEMO_USER_LOGIN);
            UserDemoModeUseCase instance = demoModePortInstance.get();
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
        User userExists = repository.find(user).orElse(null);
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

        newUser = repository.save(newUser);
        log.info("User registered: {}", newUser.getCredentials().getLogin());

        String token = generateConfirmationTokenUseCase.generateFor(newUser, null);
        String link = Properties.getAppBaseUrl() + "/api/v1/user/confirm?token=" + token;
        messagePort.sendConfirmation(newUser.getCredentials().getLogin(), link);

        UserResponse response = new UserResponse(newUser.getId());
        response.setCreated(true);
        return response;
    }
}
