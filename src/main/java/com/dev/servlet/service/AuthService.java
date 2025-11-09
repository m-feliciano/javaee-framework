package com.dev.servlet.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.request.LoginRequest;
import com.dev.servlet.domain.response.RefreshTokenResponse;
import com.dev.servlet.domain.response.UserResponse;

public interface AuthService {

    UserResponse login(LoginRequest request) throws ServiceException;

    void logout(String auth);

    String form(String auth, String onSuccess);

    RefreshTokenResponse refreshToken(String refreshToken) throws ServiceException;
}
