package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.transfer.response.UserResponse;
import com.dev.servlet.domain.transfer.request.UserCreateRequest;
import com.dev.servlet.domain.transfer.request.UserRequest;

import java.util.Optional;

/**
 * Service interface for managing user operations in the servlet application.
 * 
 * <p>This interface defines the contract for all user-related business operations,
 * including user registration, authentication, profile management, and validation.
 * It handles user lifecycle management and provides essential user services
 * for the application's security and user management features.</p>
 * 
 * @author servlets-team
 * @since 1.0
 */
public interface IUserService {
    UserResponse register(UserCreateRequest user) throws ServiceException;

    UserResponse getById(UserRequest user, String auth) throws ServiceException;

    UserResponse update(UserRequest user, String auth) throws ServiceException;

    void delete(UserRequest user, String auth) throws ServiceException;

    Optional<User> findByLoginAndPassword(String login, String password);

    boolean isEmailAvailable(String email, User candidate);
}
