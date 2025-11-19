package com.dev.servlet.controller;

import com.dev.servlet.core.annotation.Authorization;
import com.dev.servlet.core.annotation.Controller;
import com.dev.servlet.core.annotation.RequestMapping;
import com.dev.servlet.core.response.IHttpResponse;
import com.dev.servlet.domain.model.enums.RoleType;
import com.dev.servlet.domain.request.UserCreateRequest;
import com.dev.servlet.domain.request.UserRequest;
import com.dev.servlet.domain.response.UserResponse;

import static com.dev.servlet.domain.model.enums.RequestMethod.POST;

@Controller("user")
public interface UserControllerApi {

    @RequestMapping(value = "/update/{id}", method = POST, jsonType = UserRequest.class)
    IHttpResponse<UserResponse> update(UserRequest user, @Authorization String auth);

    @RequestMapping(value = "/delete/{id}", roles = RoleType.ADMIN, method = POST, jsonType = UserRequest.class)
    IHttpResponse<Void> delete(UserRequest user, @Authorization String auth);

    @RequestMapping(requestAuth = false, value = "/registerUser", method = POST, jsonType = UserCreateRequest.class)
    IHttpResponse<Void> register(UserCreateRequest user);

    @RequestMapping(value = "/list/{id}", jsonType = UserRequest.class)
    IHttpResponse<UserResponse> getUserDetail(UserRequest user, @Authorization String auth);
}

