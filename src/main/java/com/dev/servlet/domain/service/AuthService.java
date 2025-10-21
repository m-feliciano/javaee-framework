package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.transfer.request.LoginRequest;
import com.dev.servlet.domain.transfer.response.RefreshTokenResponse;
import com.dev.servlet.domain.transfer.response.UserResponse;

/**
 * Service interface for managing user authentication and session operations.
 * 
 * <p>This interface defines the contract for login and logout operations,
 * handling user authentication, session management, and security validations.
 * It serves as the main entry point for user authentication processes in
 * the application, coordinating with user services for credential validation.</p>
 * 
 * @author servlets-team
 * @since 1.0
 */
public interface AuthService {

    UserResponse login(LoginRequest request) throws ServiceException;

    void logout(String auth);

    String form(String auth, String onSuccess);

    RefreshTokenResponse refreshToken(String refreshToken) throws ServiceException;
}
