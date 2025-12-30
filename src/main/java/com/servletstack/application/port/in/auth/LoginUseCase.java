package com.servletstack.application.port.in.auth;

import com.servletstack.adapter.in.web.dto.IHttpResponse;
import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.LoginRequest;
import com.servletstack.application.transfer.response.UserResponse;

public interface LoginUseCase {
    IHttpResponse<UserResponse> login(LoginRequest request, String onSuccess) throws AppException;
}

