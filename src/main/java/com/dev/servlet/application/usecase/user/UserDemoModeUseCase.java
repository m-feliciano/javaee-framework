package com.dev.servlet.application.usecase.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.UserDemoModePort;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.domain.entity.Credentials;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.RoleType;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.utils.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.dev.servlet.shared.enums.ConstantUtils.DEMO_USER_LOGIN;
import static com.dev.servlet.shared.enums.ConstantUtils.DEMO_USER_PASSWORD;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
@ApplicationScoped
public class UserDemoModeUseCase implements UserDemoModePort {
    @Inject
    private UserRepositoryPort repositoryPort;

    @Override
    public User validateCredentials(LoginRequest credentials) throws AppException {
        log.debug("UserDemoModeUseCase: authenticating demo user {}", credentials.login());

        if (!Properties.isDemoModeEnabled()) {
            log.error("[SEVERE] Attempt to authenticate demo user {} while DEMO_MODE is disabled", credentials.login());
            throw new AppException(SC_FORBIDDEN, "Demo mode is not enabled.");
        }

        if (!DEMO_USER_LOGIN.equalsIgnoreCase(credentials.login()) ||
            !DEMO_USER_PASSWORD.equalsIgnoreCase(credentials.password())) {
            log.warn("Authentication attempt in demo mode with invalid credentials: {}", credentials.login());
            throw new AppException(SC_UNAUTHORIZED, "Invalid demo user credentials.");
        }

        User user;
        var maybe = repositoryPort.find(new User(credentials.login()));
        if (maybe.isPresent()) {
            user = maybe.get();

            if (!user.getStatus().equals(Status.ACTIVE.getValue())) {
                log.warn("Demo user {} found but is not active", credentials.login());
                throw new AppException(SC_UNAUTHORIZED, "Demo user is not active.");
            }

            if (!PasswordHasher.verify(credentials.password(), user.getCredentials().getPassword())) {
                log.warn("Demo user {} found but password does not match", credentials.login());
                throw new AppException(SC_UNAUTHORIZED, "Invalid demo user credentials.");
            }

            log.debug("UserDemoModeUseCase: DEMO_MODE - demo user {} found with id {}", credentials.login(), user.getId());
        } else {
            log.debug("UserDemoModeUseCase: DEMO_MODE - demo user not found, registering new guest user {}", credentials.login());

            User newUser = User.builder()
                    .credentials(Credentials.builder()
                            .login(credentials.login().toLowerCase())
                            .password(PasswordHasher.hash(credentials.password()))
                            .build())
                    .status(Status.ACTIVE.getValue())
                    .perfis(List.of(RoleType.DEFAULT.getCode()))
                    .build();

            user = repositoryPort.save(newUser);
            log.debug("LoginUseCase: DEMO_MODE - registered new guest user with id {}", user.getId());
        }

        log.debug("UserDemoModeUseCase: demo user {} validated successfully", credentials.login());
        return user;
    }
}
