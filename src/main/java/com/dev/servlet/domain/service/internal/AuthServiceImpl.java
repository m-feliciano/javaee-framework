package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.JwtUtil;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.service.AuditService;
import com.dev.servlet.domain.service.AuthService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.domain.transfer.response.RefreshTokenResponse;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    @Override
    public UserResponse login(LoginRequest request) throws ServiceException {
        log.trace("");
        String login = request.login();
        String password = request.password();

        User user = userService.findByLoginAndPassword(login, password).orElse(null);
        if (user == null) {
            auditService.auditFailure("user:login", login, new AuditPayload<>(request, null));
            throw new ServiceException("Invalid login or password");
        }

        UserResponse response = userMapper.toResponse(user);
        response.setToken(jwtUtil.generateAccessToken(user));
        response.setRefreshToken(jwtUtil.generateRefreshToken(user));
        auditService.auditSuccess("user:login", response.getToken(), null);
        return response;
    }

    @Override
    public void logout(String auth) {
        log.trace("");
        CacheUtils.clearAll(jwtUtil.getUserId(auth));
        auditService.auditSuccess("user:logout", auth, null);
    }

    @Override
    public String form(String auth, String onSuccess) {
        log.trace("");

        if (jwtUtil.validateToken(auth)) {
            auditService.auditSuccess("auth:form", auth, null);
            return "redirect:/" + onSuccess;
        }

        auditService.auditFailure("auth:form", auth, null);
        return "forward:pages/formLogin.jsp";
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ServiceException {
        log.trace("");
        if (!jwtUtil.validateToken(refreshToken)) {
            auditService.auditFailure("auth:refresh_token", refreshToken, null);
            throw new ServiceException("Invalid refresh token");
        }

        User user = jwtUtil.getUser(refreshToken);
        UserResponse userResponse = userService.getById(new UserRequest(user.getId()), refreshToken);
        user.setPerfis(userResponse.getPerfis());
        String newToken = jwtUtil.generateAccessToken(user);
        CacheUtils.clearAll(user.getId());
        var refreshTokenResponse = new RefreshTokenResponse(newToken);
        auditService.auditSuccess("auth:refresh_token", refreshToken, new AuditPayload<>(null, refreshTokenResponse));
        return refreshTokenResponse;
    }
}
