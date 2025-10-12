package com.dev.servlet.domain.service.internal;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.mapper.UserMapper;
import com.dev.servlet.core.util.CacheUtils;
import com.dev.servlet.core.util.CryptoUtils;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.service.ILoginService;
import com.dev.servlet.domain.service.IUserService;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@NoArgsConstructor
@Singleton
public class LoginServiceImpl implements ILoginService {

    @Inject
    private UserMapper userMapper;

    @Override
    public UserResponse login(LoginRequest request, IUserService userService) throws ServiceException {
        log.trace("");
        String login = request.login();
        String password = request.password();

        User user = userService.findByLoginAndPassword(login, password).orElse(null);
        if (user == null) return null;

        UserResponse userResponse = userMapper.toResponse(user);
        userResponse.setToken(CryptoUtils.generateJwtToken(user));
        return userResponse;
    }

    @Override
    public void logout(String auth) {
        log.trace("");
        CacheUtils.clearAll(auth);
    }
}
