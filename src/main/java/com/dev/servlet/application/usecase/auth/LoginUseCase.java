package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.adapter.in.web.dto.HttpResponse;
import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.auth.LoginPort;
import com.dev.servlet.application.port.in.user.GetUserPort;
import com.dev.servlet.application.port.in.user.UserDemoModePort;
import com.dev.servlet.application.port.out.audit.AuditPort;
import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.shared.vo.AuditPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
@NoArgsConstructor
public class LoginUseCase implements LoginPort {
    private static final String EVENT_NAME = "user:login";

    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private GetUserPort userPort;
    @Inject
    private UserDemoModePort userDemoModePort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    public IHttpResponse<UserResponse> login(LoginRequest credentials, String onSuccess) throws ApplicationException {
        final String login = credentials.login();
        final String password = credentials.password();

        log.debug("LoginUseCase: attempting login for user {}", login);

        try {
            if (Properties.isDemoModeEnabled()) {
                log.debug("LoginUseCase: DEMO_MODE is enabled, bypassing authentication for user {}", login);
                User demoUser = userDemoModePort.validateCredentials(credentials);
                UserResponse response = authenticate(demoUser);
                return HttpResponse.ok(response).next(onSuccess).build();
            }

            UserRequest userRequest = new UserRequest(login, password);
            User user = userPort.get(userRequest).orElse(null);
            if (user == null) throw new ApplicationException("Invalid login or password");

            if (Status.PENDING.equals(user.getStatus())) {
                auditPort.warning(EVENT_NAME, null, new AuditPayload<>(credentials, null));
                UserResponse userResponse = UserResponse.builder()
                        .id(user.getId())
                        .unconfirmedEmail(true)
                        .build();
                return HttpResponse.ok(userResponse).next("forward:pages/formLogin.jsp").build();
            }

            UserResponse response = authenticate(user);
            auditPort.success(EVENT_NAME, response.getToken(), null);
            return HttpResponse.ok(response).next(onSuccess).build();

        } catch (Exception e) {
            log.error("LoginUseCase: login failed for user {}: {}", login, e.getMessage());
            auditPort.warning(EVENT_NAME, null, new AuditPayload<>(credentials, null));

            return HttpResponse.<UserResponse>newBuilder()
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                    .error("Invalid login or password")
                    .reasonText("Unauthorized")
                    .next("forward:pages/formLogin.jsp")
                    .build();
        }
    }

    private UserResponse authenticate(User user) throws ApplicationException {
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
