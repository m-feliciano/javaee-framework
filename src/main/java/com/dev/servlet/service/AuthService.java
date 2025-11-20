package com.dev.servlet.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.request.LoginRequest;
import com.dev.servlet.domain.response.RefreshTokenResponse;
import com.dev.servlet.domain.response.UserResponse;

public interface AuthService {

    IHttpResponse<UserResponse> login(LoginRequest request, String onSuccess) throws ServiceException;

    void logout(String auth);

    String form(String auth, String onSuccess);

    RefreshTokenResponse refreshToken(String refreshToken) throws ServiceException;

    String homePage();

    String registerPage();
}
