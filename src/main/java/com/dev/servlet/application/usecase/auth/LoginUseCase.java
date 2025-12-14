package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.auth.LoginPort;
import com.dev.servlet.application.port.in.user.UserDemoModePort;
import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.user.GetUserPort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.utils.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
public class LoginUseCase implements LoginPort {
    @Inject
    private UserMapper userMapper;
    @Inject
    private GetUserPort userPort;
    @Inject
    private UserDemoModePort userDemoModePort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    public IHttpResponse<UserResponse> login(LoginRequest credentials, String onSuccess) throws AppException {
        final String login = credentials.login();

        try {
            if (Properties.isDemoModeEnabled()) {
                log.debug("LoginUseCase: DEMO_MODE is enabled, bypassing authentication for user {}", login);
                User demoUser = userDemoModePort.validateCredentials(credentials);
                UserResponse response = authenticate(demoUser);
                return HttpResponse.ok(response).next(onSuccess).build();
            }

            User user = userPort.get(new UserRequest(login, null))
                    .orElseThrow(() -> new AppException("Invalid login or password"));

            boolean verified = PasswordHasher.verify(credentials.password(), user.getCredentials().getPassword());
            if (!verified) throw new AppException("Invalid login or password");

            if (Status.PENDING.equals(user.getStatus())) {
                UserResponse userResponse = UserResponse.builder()
                        .id(user.getId())
                        .unconfirmedEmail(true)
                        .build();
                return HttpResponse.ok(userResponse).next("forward:pages/formLogin.jsp").build();
            }

            UserResponse response = authenticate(user);
            return HttpResponse.ok(response).next(onSuccess).build();

        } catch (Exception e) {
            log.warn("LoginUseCase: login failed for user {}: {}", login, e.getMessage());

            return HttpResponse.<UserResponse>newBuilder()
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                    .error("Invalid login or password")
                    .reasonText("Unauthorized")
                    .next("forward:pages/formLogin.jsp")
                    .build();
        }
    }

    private UserResponse authenticate(User user) throws AppException {
        log.debug("LoginUseCase: authenticating user {}", user.getId());

        UserResponse response = userMapper.toResponse(user);
        String accessToken = authenticationPort.generateAccessToken(user);
        String refreshJwt = authenticationPort.generateRefreshToken(user);
        log.debug("LoginUseCase: generated access and refresh tokens for user {}", user.getId());

        RefreshToken rt = RefreshToken.builder()
                .token(authenticationPort.stripBearerPrefix(refreshJwt))
                .user(user)
                .revoked(false)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)))
                .build();
        refreshTokenRepositoryPort.save(rt);
        log.debug("LoginUseCase: user {} logged in successfully", user.getId());

        response.setToken(accessToken);
        response.setRefreshToken(refreshJwt);
        return response;
    }
}
