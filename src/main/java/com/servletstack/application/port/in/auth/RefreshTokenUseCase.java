package com.servletstack.application.port.in.auth;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.response.RefreshTokenResponse;

public interface RefreshTokenUseCase {
    RefreshTokenResponse refreshToken(String refreshToken) throws AppException;
}

