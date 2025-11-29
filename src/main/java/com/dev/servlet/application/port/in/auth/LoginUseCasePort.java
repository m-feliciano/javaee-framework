package com.dev.servlet.application.port.in.auth;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.response.UserResponse;
import com.dev.servlet.web.response.IHttpResponse;

public interface LoginUseCasePort {
    IHttpResponse<UserResponse> login(LoginRequest request, String onSuccess) throws ApplicationException;
}

