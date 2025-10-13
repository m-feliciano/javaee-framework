package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.CryptoUtils;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.service.AuditService;
import com.dev.servlet.domain.service.ILoginService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.dev.servlet.core.util.CryptoUtils.isValidToken;

@Slf4j
@NoArgsConstructor
@Singleton
public class LoginServiceImpl implements ILoginService {

    @Inject
    private UserMapper userMapper;

    @Inject
    private AuditService auditService;

    @Override
    public UserResponse login(LoginRequest request, IUserService userService) throws ServiceException {
        log.trace("");
        String login = request.login();
        String password = request.password();

        User user = userService.findByLoginAndPassword(login, password).orElse(null);
        if (user == null) {
            auditService.auditFailure("user:login", login, new AuditPayload<>(request, null));
            throw new ServiceException("Invalid login or password");
        }

        UserResponse response = userMapper.toResponse(user);
        response.setToken(CryptoUtils.generateJwtToken(user));
        auditService.auditSuccess("user:login", response.getToken(), null);
        return response;
    }

    @Override
    public void logout(String auth) {
        log.trace("");
        CacheUtils.clearAll(auth);
        auditService.auditSuccess("user:logout", auth, null);
    }

    @Override
    public String form(String auth, String onSuccess) {
        if (isValidToken(auth)) {
            auditService.auditSuccess("auth:form", auth, null);
            return "redirect:/" + onSuccess;
        }

        auditService.auditFailure("auth:form", auth, null);
        return "forward:pages/formLogin.jsp";
    }
}
