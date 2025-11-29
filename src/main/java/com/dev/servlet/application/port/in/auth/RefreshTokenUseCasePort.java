package com.dev.servlet.application.port.in.auth;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.transfer.response.RefreshTokenResponse;

public interface RefreshTokenUseCasePort {
    RefreshTokenResponse refreshToken(String refreshToken) throws ApplicationException;
}

