package com.dev.servlet.application.usecase.auth;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.mapper.UserMapper;
import com.dev.servlet.application.port.in.auth.LoginUseCasePort;
import com.dev.servlet.application.port.in.user.GetUserUseCasePort;
import com.dev.servlet.application.port.out.AuditPort;
import com.dev.servlet.application.port.out.AuthenticationPort;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.request.UserRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.audit.AuditPayload;
import com.dev.servlet.infrastructure.persistence.repository.RefreshTokenRepository;
import com.dev.servlet.web.response.HttpResponse;
import com.dev.servlet.web.response.IHttpResponse;
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
public class LoginUseCase implements LoginUseCasePort {
    private static final String EVENT_NAME = "user:login";

    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditPort auditPort;
    @Inject
    private GetUserUseCasePort userUseCasePort;
    @Inject
    private AuthenticationPort authenticationPort;
    @Inject
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public IHttpResponse<UserResponse> login(LoginRequest request, String onSuccess) throws ApplicationException {
        String login = request.login();
        String password = request.password();

        log.debug("LoginUseCase: attempting login for user {}", login);

        try {
            UserRequest userRequest = new UserRequest(login, password);

            User user = userUseCasePort.get(userRequest).orElse(null);
            if (user == null) {
                auditPort.failure(EVENT_NAME, null, new AuditPayload<>(request, null));
                throw new ApplicationException("Invalid login or password");
            }

            if (Status.PENDING.equals(user.getStatus())) {
                auditPort.warning(EVENT_NAME, null, new AuditPayload<>(request, null));
                UserResponse userResponse = UserResponse.builder()
                        .id(user.getId())
                        .unconfirmedEmail(true)
                        .build();
                return HttpResponse.ok(userResponse).next("forward:pages/formLogin.jsp").build();
            }

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
            refreshTokenRepository.save(rt);
            log.debug("LoginUseCase: user {} logged in successfully", user.getId());

            response.setToken(accessToken);
            response.setRefreshToken(refreshJwt);

            auditPort.success(EVENT_NAME, response.getToken(), null);
            return HttpResponse.ok(response).next(onSuccess).build();

        } catch (Exception e) {
            auditPort.warning(EVENT_NAME, null, new AuditPayload<>(request, null));

            return HttpResponse.<UserResponse>newBuilder()
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                    .error("Invalid login or password")
                    .reasonText("Unauthorized")
                    .next("forward:pages/formLogin.jsp")
                    .build();
        }
    }
}
