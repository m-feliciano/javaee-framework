package com.dev.servlet.application.port.in.auth;

import com.dev.servlet.adapter.in.web.dto.IHttpResponse;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.LoginRequest;
import com.dev.servlet.application.transfer.response.UserResponse;

public interface LoginPort {
    IHttpResponse<UserResponse> login(LoginRequest request, String onSuccess) throws AppException;
}

