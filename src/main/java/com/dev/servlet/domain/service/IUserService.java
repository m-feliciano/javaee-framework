package com.dev.servlet.domain.service;

import com.dev.servlet.core.exception.ServiceException;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.transfer.request.UserCreateRequest;
import com.dev.servlet.domain.transfer.request.UserRequest;
import com.dev.servlet.domain.transfer.response.UserResponse;

import java.util.Optional;

public interface IUserService {
    UserResponse register(UserCreateRequest user) throws ServiceException;

    UserResponse getById(UserRequest user, String auth) throws ServiceException;

    UserResponse update(UserRequest user, String auth) throws ServiceException;

    void delete(UserRequest user, String auth) throws ServiceException;

    Optional<User> findByLoginAndPassword(String login, String password);

    boolean isEmailAvailable(String email, User candidate);
}
