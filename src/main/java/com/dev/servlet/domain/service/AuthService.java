package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.domain.transfer.response.RefreshTokenResponse;
import com.dev.servlet.domain.transfer.response.UserResponse;

public interface AuthService {

    UserResponse login(LoginRequest request) throws ServiceException;

    void logout(String auth);

    String form(String auth, String onSuccess);

    RefreshTokenResponse refreshToken(String refreshToken) throws ServiceException;
}
