package com.dev.servlet.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.request.UserCreateRequest;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.UserResponse;

import java.util.Optional;

public interface IUserService {
    UserResponse register(UserCreateRequest user) throws ServiceException;

    UserResponse getById(UserRequest user, String auth) throws ServiceException;

    UserResponse getUserDetail(UserRequest user, String auth) throws ServiceException;

    UserResponse update(UserRequest user, String auth) throws ServiceException;

    void delete(UserRequest user, String auth) throws ServiceException;

    Optional<User> findByLoginAndPassword(String login, String password);

    boolean isEmailAvailable(String email, User candidate);

    UserResponse confirmEmail(String token) throws ServiceException;

    void changeEmail(String token) throws ServiceException;

    void resendConfirmation(String userId) throws ServiceException;
}
