package com.dev.servlet.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.response.HttpResponse;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.model.RefreshToken;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.request.LoginRequest;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.RefreshTokenResponse;
import com.dev.servlet.domain.response.UserResponse;
import com.dev.servlet.infrastructure.persistence.dao.RefreshTokenDAO;
import com.dev.servlet.service.AuditService;
import com.dev.servlet.service.AuthService;
import com.dev.servlet.service.IUserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor
@Singleton
public class AuthServiceImpl implements AuthService {

    @Inject
    private UserMapper userMapper;
    @Inject
    private AuditService auditService;
    @Inject
    private IUserService userService;
    @Inject
    private JwtUtil jwtUtil;
    @Inject
    private RefreshTokenDAO refreshTokenDAO;

    @Override
    public IHttpResponse<UserResponse> login(LoginRequest request, String onSuccess) throws ServiceException {
        String login = request.login();
        String password = request.password();

        try {
            User user = userService.findByLoginAndPassword(login, password).orElse(null);
            if (user == null) {
                auditService.auditFailure("user:login", null, new AuditPayload<>(request, null));
                throw new ServiceException("Invalid login or password");
            }

            UserResponse response = userMapper.toResponse(user);
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshJwt = jwtUtil.generateRefreshToken(user);

            RefreshToken rt = RefreshToken.builder()
                    .token(jwtUtil.stripBearer(refreshJwt))
                    .user(user)
                    .revoked(false)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30)))
                    .build();
            refreshTokenDAO.save(rt);

            response.setToken(accessToken);
            response.setRefreshToken(refreshJwt);
            auditService.auditSuccess("user:login", response.getToken(), null);
            return HttpResponse.ok(response).next(onSuccess).build();

        } catch (Exception e) {
            auditService.auditWarning("user:login", null, new AuditPayload<>(request, null));

            return HttpResponse.<UserResponse>newBuilder()
                    .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                    .error("Invalid login or password")
                    .reasonText("Unauthorized")
                    .next("forward:pages/formLogin.jsp")
                    .build();
        }
    }

    @Override
    public void logout(String auth) {
        try {
            String userId = jwtUtil.getUserId(auth);
            refreshTokenDAO.revokeAll(userId);
            CacheUtils.clearAll(userId);
        } catch (Exception ignored) {}

        auditService.auditSuccess("user:logout", auth, null);
    }

    @Override
    public String form(String auth, String onSuccess) {

        if (jwtUtil.validateToken(auth)) {
            auditService.auditSuccess("auth:form", auth, null);
            return "redirect:/" + onSuccess;
        }

        auditService.auditWarning("auth:form", auth, null);
        return "forward:pages/formLogin.jsp";
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ServiceException {
        if (!jwtUtil.validateToken(refreshToken)) {
            auditService.auditFailure("auth:refresh_token", refreshToken, null);
            throw new ServiceException("Invalid refresh token");
        }

        String raw = jwtUtil.stripBearer(refreshToken);
        var maybe = refreshTokenDAO.findByToken(raw);
        if (maybe.isEmpty() || maybe.get().getExpiresAt() == null || maybe.get().getExpiresAt().isBefore(Instant.now())) {
            auditService.auditFailure("auth:refresh_token", refreshToken, null);
            throw new ServiceException("Refresh token is invalid or revoked");
        }

        RefreshToken old = maybe.get();
        User user = jwtUtil.getUser(refreshToken);
        UserResponse userResponse = userService.getById(new UserRequest(user.getId()), refreshToken);
        user.setPerfis(userResponse.getPerfis());

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshJwt = jwtUtil.generateRefreshToken(user);

        RefreshToken created = RefreshToken.builder()
                .token(jwtUtil.stripBearer(newRefreshJwt))
                .user(user)
                .revoked(false)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(java.util.concurrent.TimeUnit.DAYS.toSeconds(30)))
                .replacedBy(null)
                .build();
        refreshTokenDAO.save(created);

        old.setRevoked(true);
        old.setReplacedBy(created.getId());
        refreshTokenDAO.update(old);

        CacheUtils.clearAll(user.getId());
        var refreshTokenResponse = new RefreshTokenResponse(newAccessToken, newRefreshJwt);
        auditService.auditSuccess("auth:refresh_token", refreshToken, null);
        return refreshTokenResponse;
    }

    @Override
    public String homePage() {
        return "redirect:/api/v1/auth/form";
    }

    @Override
    public String registerPage() {
        return "forward:pages/user/formCreateUser.jsp";
    }
}
