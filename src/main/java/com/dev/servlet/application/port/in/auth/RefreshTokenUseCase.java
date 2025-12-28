package com.dev.servlet.application.port.in.auth;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;

public interface RefreshTokenUseCase {
    RefreshTokenResponse refreshToken(String refreshToken) throws AppException;
}

